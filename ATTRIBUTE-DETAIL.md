# Attribute Detail

<table>
    <thead>
        <tr>
            <th>Location</th>
            <th>Attribute</th>
            <th>Description</th>
            <th>Default</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <th rowspan="7">kLineChart</th>
            <th>mainIndicatorType</th>
            <th>main chart indicator type</th>
            <th>MA</th>
        </tr>
        <tr>
            <th>subIndicatorType</th>
            <th>sub-chart indicator type</th>
            <th>MACD</th>
        </tr>
        <tr>
            <th>displayVolIndicatorChart</th>
            <th>whether to display the VOL indicator chart</th>
            <th>true</th>
        </tr>
        <tr>
            <th>chartHeightSizeType</th>
            <th>chart height size type</th>
            <th>scale</th>
        </tr>
        <tr>
            <th>volChartHeight</th>
            <th>vol chart height</th>
            <th>20%</th>
        </tr>
        <tr>
            <th>indicatorChartHeight</th>
            <th>indicator chart height</th>
            <th>20%</th>
        </tr>
        <tr>
            <th>decelerationEnable</th>
            <th>it is effective to slow down when dragging</th>
            <th>true</th>
        </tr>
        <tr>
            <th rowspan="3">kLineChart.grid in kotlin or kLineChart.getGrid() in java</th>
            <th>grid_displayLine</th>
            <th>whether to display the border line for the entire chart</th>
            <th>false</th>
        </tr>
        <tr>
            <th>grid_lineSize</th>
            <th>size of the border line for the entire chart</th>
            <th>1px</th>
        </tr>
        <tr>
            <th>grid_lineColor</th>
            <th>color of the border line for the entire chart</th>
            <th>#707070</th>
        </tr>
        <tr>
            <th rowspan="16">kLineChart.candle in kotlin or kLineChart.getCandle() in java</th>
            <th>candle_increasingColor</th>
            <th>color when the price rises</th>
            <th>#5DB300</th>
        </tr>
        <tr>
            <th>candle_decreasingColor</th>
            <th>color when the price falls</th>
            <th>#FF4A4A</th>
        </tr>
        <tr>
            <th>candle_style</th>
            <th>the style of the candle</th>
            <th>SOLID</th>
        </tr>
        <tr>
            <th>candle_chartStyle</th>
            <th>the style of the chart</th>
            <th>CANDLE</th>
        </tr>
        <tr>
            <th>candle_displayHighestPriceMark</th>
            <th>whether to display the highest price tag</th>
            <th>true</th>
        </tr>
        <tr>
            <th>candle_displayLowestPriceMark</th>
            <th>whether to display the lowest price tag</th>
            <th>true</th>
        </tr>
        <tr>
            <th>candle_lowestHighestPriceMarkTextColor</th>
            <th>color of highest price and lowest price marker text</th>
            <th>#898989</th>
        </tr>
        <tr>
            <th>candle_lowestHighestPriceMarkTextSize</th>
            <th>size of highest price and lowest price marker text</th>
            <th>10sp</th>
        </tr>
        <tr>
            <th>candle_displayLastPriceMark</th>
            <th>whether to display the last price tag</th>
            <th>true</th>
        </tr>
        <tr>
            <th>candle_lastPriceMarkLineStyle</th>
            <th>style of last price marker line</th>
            <th>DASH</th>
        </tr>
        <tr>
            <th>candle_lastPriceMarkLineSize</th>
            <th>size of last price marker line</th>
            <th>1dp</th>
        </tr>
         <tr>
            <th>candle_lastPriceMarkLineColor</th>
            <th>color of last price marker line</th>
            <th>#B9B9B9</th>
        </tr>
        <tr>
            <th>candle_timeLineColor</th>
            <th>color of time line</th>
            <th>#D8D8D8</th>
        </tr>
        <tr>
            <th>candle_timeLineSize</th>
            <th>size of time line</th>
            <th>1dp</th>
        </tr>
         <tr>
            <th>candle_timeLineFillColor</th>
            <th>time line fill color</th>
            <th>#20D8D8D8</th>
        </tr>
        <tr>
            <th>candle_timeAverageLineColor</th>
            <th>time average line color</th>
            <th>#F5A623</th>
        </tr>
        <tr>
            <th rowspan="3">kLineChart.indicator in kotlin or kLineChart.getIndicator()</th>
            <th>indicator_lineSize</th>
            <th>size of indicator line</th>
            <th>1dp</th>
        </tr>
        <tr>
            <th>indicator_increasingColor</th>
            <th>color when the price rises</th>
            <th>#5DB300</th>
        </tr>
        <tr>
            <th>indicator_decreasingColor</th>
            <th>color when the price fall</th>
            <th>#FF4A4A</th>
        </tr>
        <tr>
            <th rowspan="18">kLineChart.tooltip in kotlin or kLineChart.getTooltip() in java</th>
            <th>tooltip_crossLineStyle</th>
            <th>style of the cross cursor line</th>
            <th>SOLID</th>
        </tr>
        <tr>
            <th>tooltip_crossLineSize</th>
            <th>size of the cross cursor line</th>
            <th>1px</th>
        </tr>
        <tr>
            <th>tooltip_crossLineColor</th>
            <th>color of the cross cursor line</th>
            <th>#505050</th>
        </tr>
        <tr>
            <th>tooltip_crossTextRectStrokeLineSize</th>
            <th>size of the cross cursor line text border</th>
            <th>1dp</th>
        </tr>
        <tr>
            <th>tooltip_crossTextRectStrokeLineColor</th>
            <th>color of the cross cursor line text border</th>
            <th>#EDEDED</th>
        </tr>
        <tr>
            <th>tooltip_crossTextRectFillColor</th>
            <th>cross cursor line text box fill color</th>
            <th>#505050</th>
        </tr>
        <tr>
            <th>tooltip_crossTextColor</th>
            <th>color of the cross cursor line text</th>
            <th>#EDEDED</th>
        </tr>
        <tr>
            <th>tooltip_crossTextSize</th>
            <th>size of the cross cursor line text</th>
            <th>10sp</th>
        </tr>
        <tr>
            <th>tooltip_crossTextMarginSpace</th>
            <th>the margin around the text of the cross cursor line</th>
            <th>3dp</th>
        </tr>
        <tr>
            <th>tooltip_generalDataRectStrokeLineSize</th>
            <th>the border size of the prompt box</th>
            <th>1dp</th>
        </tr>
        <tr>
            <th>tooltip_generalDataRectStrokeLineColor</th>
            <th>the border color of the prompt box</th>
            <th>#505050</th>
        </tr>
        <tr>
            <th>tooltip_generalDataRectFillColor</th>
            <th>fill color of the prompt box</th>
            <th>#99000000</th>
        </tr>
        <tr>
            <th>tooltip_generalDataTextSize</th>
            <th>text size in prompt box</th>
            <th>10sp</th>
        </tr>
        <tr>
            <th>tooltip_generalDataTextColor</th>
            <th>text color in prompt box</th>
            <th>#EDEDED</th>
        </tr>
        <tr>
            <th>tooltip_generalDataIncreasingColor</th>
            <th>when the prompt box rises in the text color</th>
            <th>#5DB300</th>
        </tr>
        <tr>
            <th>tooltip_generalDataDecreasingColor</th>
            <th>when the prompt box fall in the text color</th>
            <th>#FF4A4A</th>
        </tr>
         <tr>
            <th>tooltip_indicatorDisplayRule</th>
            <th>indicator data prompt rules</th>
            <th>ALWAYS</th>
        </tr>
        <tr>
            <th>tooltip_indicatorTextSize</th>
            <th>indicator data prompt text size</th>
            <th>10sp</th>
        </tr>
        <tr>
            <th rowspan="15">kLineChart.xAxis in kotlin or kLineChart.getXAxis() in java</th>
            <th>xaxis_displayAxisLine</th>
            <th>whether to display the x-axis line</th>
            <th>true</th>
        </tr>
        <tr>
            <th>xaxis_axisLineColor</th>
            <th>color of x-axis line</th>
            <th>#707070</th>
        </tr>
        <tr>
            <th>xaxis_axisLineSize</th>
            <th>size of x-axis line</th>
            <th>1px</th>
        </tr>
        <tr>
            <th>xaxis_displayTickText</th>
            <th>whether to display the x-axis tick text</th>
            <th>true</th>
        </tr>
        <tr>
            <th>xaxis_tickTextColor</th>
            <th>color of x-axis tick text</th>
            <th>#707070</th>
        </tr>
        <tr>
            <th>xaxis_tickTextSize</th>
            <th>size of x-axis tick text</th>
            <th>10sp</th>
        </tr>
        <tr>
            <th>xaxis_displayTickLine</th>
            <th>whether to display the x-axis tick line</th>
            <th>true</th>
        </tr>
        <tr>
            <th>xaxis_tickLineSize</th>
            <th>size of x-axis tick line</th>
            <th>3dp</th>
        </tr>
        <tr>
            <th>xaxis_displaySeparatorLine</th>
            <th>whether to display the x-axis split line</th>
            <th>false</th>
        </tr>
        <tr>
            <th>xaxis_separatorLineSize</th>
            <th>size of x-axis split line</th>
            <th>1px</th>
        </tr>
        <tr>
            <th>xaxis_separatorLineColor</th>
            <th>color of x-axis split line</th>
            <th>#B8B8B8</th>
        </tr>
        <tr>
            <th>xaxis_separatorLineStyle</th>
            <th>the style of x-axis split line</th>
            <th>DASH</th>
        </tr>
        <tr>
            <th>xaxis_textMarginSpace</th>
            <th>margins of text on the x-axis</th>
            <th>3dp</th>
        </tr>
        <tr>
            <th>xaxis_axisMaxHeight</th>
            <th>maximum height of the x-axis</th>
            <th>20dp</th>
        </tr>
        <tr>
            <th>xaxis_axisMinHeight</th>
            <th>minimum height of the x-axis</th>
            <th>20dp</th>
        </tr>
        <tr>
            <th rowspan="17">kLineChart.yAxis in kotlin or kLineChart.getYAxis() in java</th>
            <th>yaxis_displayAxisLine</th>
            <th>whether to display the y-axis line</th>
            <th>true</th>
        </tr>
        <tr>
            <th>yaxis_axisLineColor</th>
            <th>color of y-axis line</th>
            <th>#707070</th>
        </tr>
        <tr>
            <th>yaxis_axisLineSize</th>
            <th>size of y-axis line</th>
            <th>1px</th>
        </tr>
        <tr>
            <th>yaxis_displayTickText</th>
            <th>whether to display the y-axis tick text</th>
            <th>true</th>
        </tr>
        <tr>
            <th>yaxis_tickTextColor</th>
            <th>color of y-axis tick text</th>
            <th>#707070</th>
        </tr>
        <tr>
            <th>yaxis_tickTextSize</th>
            <th>size of y-axis tick text</th>
            <th>10sp</th>
        </tr>
        <tr>
            <th>yaxis_displayTickLine</th>
            <th>whether to display the y-axis tick line</th>
            <th>false</th>
        </tr>
        <tr>
            <th>yaxis_tickLineSize</th>
            <th>size of y-axis tick line</th>
            <th>3dp</th>
        </tr>
        <tr>
            <th>yaxis_displaySeparatorLine</th>
            <th>whether to display the y-axis split line</th>
            <th>false</th>
        </tr>
        <tr>
            <th>yaxis_separatorLineSize</th>
            <th>size of y-axis split line</th>
            <th>1px</th>
        </tr>
        <tr>
            <th>yaxis_separatorLineColor</th>
            <th>color of y-axis split line</th>
            <th>#B8B8B8</th>
        </tr>
        <tr>
            <th>yaxis_separatorLineStyle</th>
            <th>the style of y-axis split line</th>
            <th>DASH</th>
        </tr>
        <tr>
            <th>yaxis_textMarginSpace</th>
            <th>margins of text on the y-axis</th>
            <th>3dp</th>
        </tr>
        <tr>
            <th>yaxis_textPosition</th>
            <th>the position of the text on the y-axis</th>
            <th>INSIDE</th>
        </tr>
        <tr>
            <th>yaxis_axisPosition</th>
            <th>position of the y-axis</th>
            <th>RIGHT</th>
        </tr>
        <tr>
            <th>yaxis_axisMaxWidth</th>
            <th>maximum width of the y-axis</th>
            <th>20dp</th>
        </tr>
        <tr>
            <th>yaxis_axisMinWidth</th>
            <th>minimum width of the y-axis</th>
            <th>20dp</th>
        </tr>
    </tbody>
</table>