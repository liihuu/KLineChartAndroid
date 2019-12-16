# Android版K线图
支持自定义样式，支持自定义绘制，支持值格式化，支持指标设置，支持自定义指标，默认指标支持MA, VOL, MACD, BOLL, KDJ, KD, RSI, BIAS, BRAR, CCI, DMI, CR, PSY, DMA, TRIX, OBV, VR, WR, MTM, EMV, SAR。

## 常量
1. 线样式：Component.LineStyle。
2. 蜡烛图样式：Candle.CandleStyle。
3. 主图类型：Candle.ChartStyle。
4. 默认指标类型：Indicator.Type。
5. 指标数值显示规则：Tooltip.IndicatorDisplayRule。
6. Y轴位置：YAxis.AxisPosition。
7. Y轴文字位置：YAxis.TextPosition。

## 示例代码
1. [基本使用](../app/src/main/java/com/liihuu/kline/BasicKLineActivity.kt)。
2. [样式设置](../app/src/main/java/com/liihuu/kline/StyleSettingActivity.kt)。
3. [自定义绘制蜡烛图上的价格标示](../app/src/main/java/com/liihuu/kline/DrawPriceMarkActivity.kt)
4. [指标设置](../app/src/main/java/com/liihuu/kline/IndicatorSettingActivity.kt)
5. [自定义指标](../app/src/main/java/com/liihuu/kline/CustomIndicatorActivity.kt)
6. [自定绘制基础信息展示](../app/src/main/java/com/liihuu/kline/DrawTooltipGeneralDataActivity.kt)
7. [自定义格式化基础信息展示](../app/src/main/java/com/liihuu/kline/CustomTooltipGeneralDataActivity.kt)
8. [值格式](../app/src/main/java/com/liihuu/kline/ValueFormatActivity.kt)
9. [分段加载数据](../app/src/main/java/com/liihuu/kline/SegmentLoadDataActivity.kt)
10. [分段加载数据带加载动画](../app/src/main/java/com/liihuu/kline/SegmentLoadDataWithAnimActivity.kt)
11. [添加数据](../app/src/main/java/com/liihuu/kline/FillDataActivity.kt)
12. [图表放在滚动控件中](../app/src/main/java/com/liihuu/kline/ScrollingActivity.kt)
