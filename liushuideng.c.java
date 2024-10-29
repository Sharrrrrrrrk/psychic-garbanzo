#include <reg52.h>

int led[] = {0xfe, 0xfd, 0xfb, 0xf7, 0xef, 0xdf, 0xbf, 0x7f}; // 8个LED的模式
int i = 0;
bit direction = 1;           // 流水方向，1表示从左往右，0表示从右往左
unsigned int overflow_count = 0;  // 用于累计50ms中断次数

void Timer0_Init() {
    TMOD = 0x01;     // 定时器0模式1（16位定时器）
    TH0 = (65536 - 46080) / 256;  // 高8位初值
    TL0 = (65536 - 46080) % 256;  // 低8位初值
    ET0 = 1;         // 允许定时器0中断
    EA = 1;          // 允许总中断
    TR0 = 1;         // 启动定时器0
}

void Timer0_ISR() interrupt 1 {
    TH0 = (65536 - 46080) / 256;  // 重装高8位初值
    TL0 = (65536 - 46080) % 256;  // 重装低8位初值

    overflow_count++;  // 累计中断次数

    if (overflow_count >= 4) {  // 达到4次50ms中断，即200ms
        overflow_count = 0;     // 计数清零

        P2 = 0x80;              // 控制端口
        P0 = led[i];            // 更新LED显示

        // 更新下一个LED的索引
        if (direction) {    // 如果方向是从左往右
            if (i < 7) {    // 只在未到达最右边时增加索引
                i++;
            } else {        // 到达最右边，改变方向
                direction = 0;
            }
        } else {            // 如果方向是从右往左
            if (i > 0) {    // 只在未到达最左边时减少索引
                i--;
            } else {        // 到达最左边，改变方向
                direction = 1;
            }
        }
    }
}

void main() {
    Timer0_Init();  // 初始化定时器0
    while (1) {
    }
}