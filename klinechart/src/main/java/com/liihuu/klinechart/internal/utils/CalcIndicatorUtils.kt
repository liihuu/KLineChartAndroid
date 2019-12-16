package com.liihuu.klinechart.internal.utils

import com.liihuu.klinechart.model.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * @Author lihu hu_li888@foxmail.com
 * @Date 2019-09-22-16:02
 */
internal object CalcIndicatorUtils {
    /**
     * 计算均线
     * @param dataList MutableList<KLineModel>
     * @return MutableList<KLineModel>
     */
    fun calcMa(dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        var ma5Num = 0.0
        var ma10Num = 0.0
        var ma20Num = 0.0
        var ma60Num = 0.0

        var ma5: Double
        var ma10: Double
        var ma20: Double
        var ma60: Double
        return calc(dataList) {i ->
            val close = dataList[i].closePrice
            ma5Num += close
            ma10Num += close
            ma20Num += close
            ma60Num += close
            if (i < 5) {
                ma5 = ma5Num / (i + 1)
            } else {
                ma5Num -= dataList[i - 5].closePrice
                ma5 = ma5Num / 5
            }

            if (i < 10) {
                ma10 = ma10Num / (i + 1)
            } else {
                ma10Num -= dataList[i - 10].closePrice
                ma10 = ma10Num / 10
            }

            if (i < 20) {
                ma20 = ma20Num / (i + 1)
            } else {
                ma20Num -= dataList[i - 20].closePrice
                ma20 = ma20Num / 20
            }

            if (i < 60) {
                ma60 = ma60Num / (i + 1)
            } else {
                ma60Num -= dataList[i - 60].closePrice
                ma60 = ma60Num / 60
            }

            dataList[i].ma = MaModel(ma5, ma10, ma20, ma60)
        }
    }

    /**
     * 计算成交量均线
     * @param dataList MutableList<KLineModel>
     * @return MutableList<KLineModel>
     */
    fun calcVol(dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        var ma5s = 0.0
        var ma10s = 0.0
        var ma20s = 0.0
        var ma5: Double
        var ma10: Double
        var ma20: Double
        return calc(dataList) {i->
            val num = dataList[i].volume
            ma5s += num
            ma10s += num
            ma20s += num

            if (i < 5) {
                ma5 = ma5s / (i + 1)
            } else {
                ma5s -= dataList[i - 5].volume
                ma5 = ma5s / 5
            }

            if (i < 10) {
                ma10 = ma10s / (i + 1)
            } else {
                ma10s -= dataList[i - 10].volume
                ma10 = ma10s / 10
            }

            if (i < 20) {
                ma20 = ma20s / (i + 1)
            } else {
                ma20s -= dataList[i - 20].volume
                ma20 = ma20s / 20
            }

            dataList[i].vol = VolModel(num, ma5, ma10, ma20)
        }
    }

    /**
     * 计算macd
     *
     * MACD：参数快线移动平均、慢线移动平均、移动平均，
     * 参数值12、26、9。
     * 公式：
     * ⒈首先分别计算出收盘价12日指数平滑移动平均线与26日指数平滑移动平均线，分别记为EMA(12）与EMA(26）。
     * ⒉求这两条指数平滑移动平均线的差，即：DIFF=EMA（SHORT）－EMA（LONG）。
     * ⒊再计算DIFF的M日的平均的指数平滑移动平均线，记为DEA。
     * ⒋最后用DIFF减DEA，得MACD。MACD通常绘制成围绕零轴线波动的柱形图。MACD柱状大于0红色，小于0绿色。
     *
     * @param dataList MutableList<KLineModel>
     * @return MutableList<KLineModel>
     */
    fun calcMacd(dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        var ema12: Double
        var ema26: Double
        var oldEma12 = 0.0
        var oldEma26 = 0.0
        var diff: Double
        var dea: Double
        var oldDea = 0.0
        var macd: Double
        return calc(dataList) {i ->
            val closePrice = dataList[i].closePrice
            if (i == 0) {
                ema12 = closePrice
                ema26 = closePrice
            } else {
                ema12 = (2 * closePrice + 11 * oldEma12) / 13f
                ema26 = (2 * closePrice + 25 * oldEma26) / 27f
            }

            diff = ema12 - ema26
            dea = (diff * 2 + oldDea * 8) / 10.0
            macd = (diff - dea) * 2
            oldEma12 = ema12
            oldEma26 = ema26
            oldDea = dea

            dataList[i].macd = MacdModel(diff, dea, macd)
        }
    }

    /**
     * 计算boll
     *
     * 参数20
     * @param dataList MutableList<KLineModel>
     * @return MutableList<KLineModel>
     */
    fun calcBoll(dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        var close20 = 0.0 // MA sum

        var ma: Double// 中轨线
        var md: Double // 标准差
        var up: Double // 上轨线
        var dn: Double // 下轨线

        return calc(dataList) { i ->
            val closePrice = dataList[i].closePrice
            close20 += closePrice
            if (i < 20) {
                ma = close20 / (i + 1)
                md = getBollMd(dataList.subList(0, i + 1), ma)
            } else {
                close20 -= dataList[i - 20].closePrice
                ma = close20 / 20
                md = getBollMd(dataList.subList(i - 19, i + 1), ma)
            }
            up = ma + 2 * md
            dn = ma - 2 * md

            dataList[i].boll = BollModel(up, ma, dn)
        }
    }

    /**
     * 计算kdj
     * @param dataList MutableList<KLineModel>
     * @return MutableList<KLineModel>
     */
    fun calcKdj(dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        var k: Double
        var d: Double
        var j: Double

        // n日内最低价
        var ln: Double
        // n日内最高价
        var hn: Double

        return calc(dataList) { i ->
            // n日收盘价
            val cn = dataList[i].closePrice

            if (i < 8) {
                ln = getLow(dataList.subList(0, i + 1))
                hn = getHigh(dataList.subList(0, i + 1))
            } else {
                ln = getLow(dataList.subList(i - 8, i + 1))
                hn = getHigh(dataList.subList(i - 8, i + 1))
            }
            val rsv = (cn - ln) / (if (hn - ln == 0.0) 1.0 else hn - ln) * 100
            // 当日K值=2/3×前一日K值+1/3×当日RSV
            // 当日D值=2/3×前一日D值+1/3×当日K值
            // 若无前一日K 值与D值，则可分别用50来代替。
            // J值=3*当日K值-2*当日D值
            k = 2.0 / 3.0 * (if (i < 8) 50.0 else dataList[i - 1].kdj?.k ?: 50.0) + 1.0 / 3.0 * rsv
            d = 2.0 / 3.0 * (if (i < 8) 50.0 else dataList[i - 1].kdj?.d ?: 50.0) + 1.0 / 3.0 * k
            j = 3.0 * k - 2.0 * d

            dataList[i].kdj =  KdjModel(k, d, j)
        }
    }

    /**
     * 计算rsi
     * @param dataList MutableList<KLineModel>
     * @return MutableList<KLineModel>
     */
    fun calcRsi(dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        // N日RSI =
        // N日内收盘涨幅的平均值/(N日内收盘涨幅均值+N日内收盘跌幅均值) ×100%
        var rsi1 = 0.0// 参数6
        var rsi2 = 0.0// 参数12
        var rsi3 = 0.0// 参数24

        var sumCloseA1 = 0.0
        var sumCloseB1 = 0.0

        var sumCloseA2 = 0.0
        var sumCloseB2 = 0.0

        var sumCloseA3 = 0.0
        var sumCloseB3 = 0.0

        var a1: Double
        var b1: Double

        var a2: Double
        var b2: Double

        var a3: Double
        var b3: Double

        return calc(dataList) { i ->
            if (i > 0) {
                val tmp = dataList[i].closePrice - dataList[i - 1].closePrice
                if (tmp > 0) {
                    sumCloseA1 += tmp
                    sumCloseA2 += tmp
                    sumCloseA3 += tmp
                } else {
                    val absTmp = abs(tmp)
                    sumCloseB1 += absTmp
                    sumCloseB2 += absTmp
                    sumCloseB3 += absTmp
                }

                if (i < 6) {
                    a1 = sumCloseA1 / (i + 1)
                    b1 = (sumCloseA1 + sumCloseB1) / (i + 1)
                } else {
                    if (i > 6) {
                        val agoTmp = dataList[i - 6].closePrice - dataList[i - 7].closePrice
                        if (agoTmp > 0) {
                            sumCloseA1 -= agoTmp
                        } else {
                            sumCloseB1 -= abs(agoTmp)
                        }
                    }
                    a1 = sumCloseA1 / 6
                    b1 = (sumCloseA1 + sumCloseB1) / 6
                }
                rsi1 = if (b1 != 0.0) {
                    a1 / b1 * 100
                } else {
                    0.0
                }

                if (i < 12) {
                    a2 = sumCloseA2 / (i + 1)
                    b2 = (sumCloseA2 + sumCloseB2) / (i + 1)
                } else {
                    if (i > 12) {
                        val agoTmp = dataList[i - 12].closePrice - dataList[i - 13].closePrice
                        if (agoTmp > 0) {
                            sumCloseA2 -= agoTmp
                        } else {
                            sumCloseB2 -= abs(agoTmp)
                        }
                    }
                    a2 = sumCloseA2 / 12
                    b2 = (sumCloseA2 + sumCloseB2) / 12
                }
                rsi2 = if (b2 != 0.0) {
                    a2 / b2 * 100
                } else {
                    0.0
                }

                if (i < 24) {
                    a3 = sumCloseA3 / (i + 1)
                    b3 = (sumCloseA3 + sumCloseB3) / (i + 1)
                } else {
                    if (i > 24) {
                        val agoTmp = dataList[i - 24].closePrice - dataList[i - 25].closePrice
                        if (agoTmp > 0) {
                            sumCloseA3 -= agoTmp
                        } else {
                            sumCloseB3 -= abs(agoTmp)
                        }
                    }
                    a3 = sumCloseA3 / 24
                    b3 = (sumCloseA3 + sumCloseB3) / 24
                }
                rsi3 = if (b3 != 0.0) {
                    a3 / b3 * 100
                } else {
                    0.0
                }
            }
            dataList[i].rsi = RsiModel(rsi1, rsi2, rsi3)
        }
    }

    /**
     * 计算bias
     * @param dataList MutableList<KLineModel>
     * @return MutableList<KLineModel>
     */
    fun calcBias(dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        // 乖离率=[(当日收盘价-N日平均价)/N日平均价]*100%
        // 参数：6，12、24
        var bias1: Double
        var bias2: Double
        var bias3: Double

        var mean1: Double
        var mean2: Double
        var mean3: Double

        var closes1 = 0.0
        var closes2 = 0.0
        var closes3 = 0.0

        return calc(dataList) { i ->
            val closePrice = dataList[i].closePrice
            closes1 += closePrice
            closes2 += closePrice
            closes3 += closePrice

            if (i < 6) {
                mean1 = closes1 / (i + 1)
            } else {
                closes1 -= dataList[i - 6].closePrice
                mean1 = closes1 / 6
            }
            bias1 = (closePrice - mean1) / mean1 * 100

            if (i < 12) {
                mean2 = closes2 / (i + 1)
            } else {
                closes2 -= dataList[i - 12].closePrice
                mean2 = closes2 / 12
            }
            bias2 = (closePrice - mean2) / mean2 * 100

            if (i < 24) {
                mean3 = closes3 / (i + 1)
            } else {
                closes3 -= dataList[i - 24].closePrice
                mean3 = closes3 / 24
            }
            bias3 = ((closePrice - mean3) / mean3) * 100
            dataList[i].bias = BiasModel(bias1, bias2, bias3)
        }
    }

    /**
     * 计算brar
     *
     * 参数是26。
     * 公式N日BR=N日内（H－CY）之和除以N日内（CY－L）之和*100，
     * 其中，H为当日最高价，L为当日最低价，CY为前一交易日的收盘价，N为设定的时间参数。
     * N日AR=(N日内（H－O）之和除以N日内（O－L）之和)*100，
     * 其中，H为当日最高价，L为当日最低价，O为当日开盘价，N为设定的时间参数
     *
     * @param dataList MutableList<KLineModel>
     * @return MutableList<KLineModel>
     */
    fun calcBrar(dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        var br = 0.0
        var ar = 0.0
        var hcy = 0.0
        var cyl = 0.0
        var ho = 0.0
        var ol = 0.0

        return calc(dataList) { i ->
            val highestPrice = dataList[i].highPrice
            val lowestPrice = dataList[i].lowPrice
            val openPrice = dataList[i].openPrice
            ho += (highestPrice - openPrice)
            ol += (openPrice - lowestPrice)
            if (i > 0) {
                val refClosePrice = dataList[i - 1].closePrice
                hcy += (highestPrice - refClosePrice)
                cyl += (refClosePrice - lowestPrice)
                if (i > 25) {
                    val agoHighestPrice = dataList[i - 26].highPrice
                    val agoLowestPrice = dataList[i - 26].lowPrice
                    val agoOpenPrice = dataList[i - 26].openPrice
                    if (i > 26) {
                        val agoRefClosePrice = dataList[i - 27].closePrice
                        hcy -= (agoHighestPrice - agoRefClosePrice)
                        cyl -= (agoRefClosePrice - agoLowestPrice)
                    }
                    ho -= (agoHighestPrice - agoOpenPrice)
                    ol -= (agoOpenPrice - agoLowestPrice)
                }
                ar = if (ol != 0.0) ho / ol * 100 else 0.0

                br = if (cyl != 0.0) hcy / cyl * 100 else 0.0
            }
            dataList[i].brar = BrarModel(br, ar)
        }
    }

    /**
     * 计算cci
     * CCI（N日）=（TP－MA）÷MD÷0.015
     * 其中，TP=（最高价+最低价+收盘价）÷3
     * MA=近N日收盘价的累计之和÷N
     * MD=最近n日 (MA - TP)的绝对值的累计和 ÷ N
     *
     * 参数14
     *
     * @param dataList MutableList<KLineModel>
     * @return MutableList<KLineModel>
     */
    fun calcCci(dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        var closes = 0.0
        var closeMa: Double
        val closeMaList = mutableListOf<Double>()
        var md: Double
        var maCloseSum = 0.0
        var cci: Double

        return calc(dataList) { i ->
            val closePrice = dataList[i].closePrice
            closes += closePrice

            val tp = (dataList[i].highPrice + dataList[i].lowPrice + closePrice) / 3
            if (i < 13) {
                closeMa = closes / (i + 1)
                maCloseSum += abs(closeMa - closePrice)
                closeMaList.add(closeMa)
                md = maCloseSum / (i + 1)
            } else {
                val agoClosePrice = dataList[i - 13].closePrice
                closes -= agoClosePrice
                closeMa = closes / 13
                closeMaList.add(closeMa)
                maCloseSum += abs(closeMa - closePrice)
                maCloseSum -= abs(closeMaList[i - 13] - agoClosePrice)
                md = maCloseSum / 13
            }
            cci = if (md != 0.0) {
                (tp - closeMa) / md / 0.015
            } else {
                0.0
            }
            dataList[i].cci = CciModel(cci)
        }
    }

    /**
     * 计算dmi
     *
     * 参数 14，6
     * MTR:=EXPMEMA(MAX(MAX(HIGH-LOW,ABS(HIGH-REF(CLOSE,1))),ABS(REF(CLOSE,1)-LOW)),N)
     * HD :=HIGH-REF(HIGH,1);
     * LD :=REF(LOW,1)-LOW;
     * DMP:=EXPMEMA(IF(HD>0&&HD>LD,HD,0),N);
     * DMM:=EXPMEMA(IF(LD>0&&LD>HD,LD,0),N);
     *
     * PDI: DMP*100/MTR;
     * MDI: DMM*100/MTR;
     * ADX: EXPMEMA(ABS(MDI-PDI)/(MDI+PDI)*100,MM);
     * ADXR:EXPMEMA(ADX,MM);
     *
     * 公式含义：
     * MTR赋值:最高价-最低价和最高价-昨收的绝对值的较大值和昨收-最低价的绝对值的较大值的N日指数平滑移动平均 
     * HD赋值:最高价-昨日最高价 
     * LD赋值:昨日最低价-最低价 
     * DMP赋值:如果HD>0并且HD>LD,返回HD,否则返回0的N日指数平滑移动平均 
     * DMM赋值:如果LD>0并且LD>HD,返回LD,否则返回0的N日指数平滑移动平均 
     * 输出PDI: DMP*100/MTR 
     * 输出MDI: DMM*100/MTR 
     * 输出ADX: MDI-PDI的绝对值/(MDI+PDI)*100的MM日指数平滑移动平均 
     * 输出ADXR:ADX的MM日指数平滑移动平均
     *
     * @param dataList MutableList<KLineModel>
     * @return MutableList<KLineModel>
     */
    fun calcDmi(dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        var pdi = 0.0
        var mdi = 0.0
        var adx = 0.0
        var adxr = 0.0

        val trList = mutableListOf(0.0)
        var trSum = 0.0
        val dmpList = mutableListOf(0.0)
        var dmpSum = 0.0
        val dmmList = mutableListOf(0.0)
        var dmmSum = 0.0
        val dxList = mutableListOf(0.0)
        var dxSum = 0.0

        return calc(dataList) { i ->
            if (i > 0) {
                val refClose = dataList[i - 1].closePrice
                val highPrice = dataList[i].highPrice
                val lowPrice = dataList[i].lowPrice
                val hl = highPrice - lowPrice
                val hcy = abs(highPrice - refClose)
                val lcy = abs(lowPrice - refClose)
                val hhy = highPrice - dataList[i - 1].highPrice
                val lyl = dataList[i - 1].lowPrice - lowPrice
                val tr = max(max(hl, hcy), lcy)
                trSum += tr
                trList.add(tr)

                val h = if (hhy > 0.0 && hhy > lyl) {
                    hhy
                } else {
                    0.0
                }
                dmpSum += h
                dmpList.add(h)

                val l = if (lyl > 0 && lyl > hhy) {
                    lyl
                } else {
                    0.0
                }
                dmmSum += l
                dmmList.add(l)

                if (i > 13) {
                    trSum -= trList[i - 14]
                    dmpSum -= dmpList[i - 14]
                    dmmSum -= dmmList[i - 14]
                }

                if (trSum == 0.0) {
                    pdi = 0.0
                    mdi = 0.0
                } else {
                    pdi = dmpSum * 100 / trSum
                    mdi = dmmSum * 100 / trSum
                }

                val dx = abs((mdi - pdi)) / (mdi + pdi) * 100
                dxSum += dx
                dxList.add(dx)
                if (i < 6) {
                    adx = dxSum / (i + 1)
                    adxr = adx
                } else {
                    val agoAdx = dxList[i - 6]
                    dxSum -= agoAdx
                    adx = dxSum / 6
                    adxr = (adx + agoAdx) / 2
                }
            }

            dataList[i].dmi = DmiModel(pdi, mdi, adx, adxr)
        }
    }

    /**
     * 计算cr
     *
     * 参数26、10、20、40、60
     * MID:=REF(HIGH+LOW,1)/2;
     * CR:SUM(MAX(0,HIGH-MID),N)/SUM(MAX(0,MID-LOW),N)*100;
     * MA1:REF(MA(CR,M1),M1/2.5+1);
     * MA2:REF(MA(CR,M2),M2/2.5+1);
     * MA3:REF(MA(CR,M3),M3/2.5+1);
     * MA4:REF(MA(CR,M4),M4/2.5+1);
     * MID赋值:(昨日最高价+昨日最低价)/2
     * 输出带状能量线:0和最高价-MID的较大值的N日累和/0和MID-最低价的较大值的N日累和*100
     * 输出MA1:M1(5)/2.5+1日前的CR的M1(5)日简单移动平均
     * 输出MA2:M2(10)/2.5+1日前的CR的M2(10)日简单移动平均
     * 输出MA3:M3(20)/2.5+1日前的CR的M3(20)日简单移动平均
     * 输出MA4:M4/2.5+1日前的CR的M4日简单移动平均
     *
     * @param dataList MutableList<KLineModel>
     * @return MutableList<KLineModel>
     */
    fun calcCr(dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        var cr = 0.0
        var ma1: Double
        var ma2: Double
        var ma3: Double
        var ma4: Double
        var p1 = 0.0
        var p2 = 0.0
        var ma10Sum = 0.0
        var ma10: Double
        val ma10List = mutableListOf<Double>()
        var ma20Sum = 0.0
        var ma20: Double
        val ma20List = mutableListOf<Double>()
        var ma40Sum = 0.0
        var ma40: Double
        val ma40List = mutableListOf<Double>()
        var ma60Sum = 0.0
        var ma60: Double
        val ma60List = mutableListOf<Double>()

        return calc(dataList) { i ->
            if (i > 0) {
                val preHighestPrice = dataList[i - 1].highPrice
                val preLowestPrice = dataList[i - 1].lowPrice
                val preClosePrice = dataList[i - 1].closePrice
                val preOpenPrice = dataList[i - 1].openPrice
                val preMidPrice = (preHighestPrice + preClosePrice + preLowestPrice + preOpenPrice) / 4

                val highestPrice = dataList[i].highPrice
                val lowestPrice = dataList[i].lowPrice

                var highSubPreMid = highestPrice - preMidPrice
                if (highSubPreMid < 0.0) {
                    highSubPreMid = 0.0
                }
                p1 += highSubPreMid

                var preMidSubLow = preMidPrice - lowestPrice
                if (preMidSubLow < 0.0) {
                    preMidSubLow = 0.0
                }
                p2 += preMidSubLow

                if (i > 26) {
                    val firstHighestPrice = dataList[i - 27].highPrice
                    val firstLowestPrice = dataList[i - 27].lowPrice
                    val firstClosePrice = dataList[i - 27].closePrice
                    val firstOpenPrice = dataList[i - 27].openPrice
                    val firstMidPrice = (firstHighestPrice + firstLowestPrice + firstClosePrice + firstOpenPrice) / 4

                    val secondHighestPrice = dataList[i - 26].highPrice
                    val secondLowestPrice = dataList[i - 26].lowPrice

                    var secondHighSubFirstMid = secondHighestPrice - firstMidPrice
                    if (secondHighSubFirstMid < 0.0) {
                        secondHighSubFirstMid = 0.0
                    }

                    var firstMidSubSecondLow = firstMidPrice - secondLowestPrice
                    if (firstMidSubSecondLow < 0.0) {
                        firstMidSubSecondLow = 0.0
                    }
                    p1 -= secondHighSubFirstMid
                    p2 -= firstMidSubSecondLow
                }

                if (p2 != 0.0) {
                    cr = p1 / p2 * 100
                }

                val ym = (dataList[i - 1].highPrice + dataList[i - 1].lowPrice + dataList[i - 1].closePrice) / 3
                val hym = dataList[i].highPrice - ym
                p1 += (if (0.0 >= hym) 0.0 else hym)
                val lym = ym - dataList[i].lowPrice
                p2 += (if (0.0 >= lym) 0.0 else lym)
            }
            ma10Sum += cr
            ma20Sum += cr
            ma40Sum += cr
            ma60Sum += cr

            if (i < 10) {
                ma10 = ma10Sum / (i + 1)
            } else {
                ma10Sum -= dataList[i - 10].cr?.cr ?: 0.0
                ma10 = ma10Sum / 10
            }
            ma10List.add(ma10)

            if (i < 20) {
                ma20 = ma20Sum / (i + 1)
            } else {
                ma20Sum -= dataList[i - 20].cr?.cr ?: 0.0
                ma20 = ma20Sum / 20
            }
            ma20List.add(ma20)

            if (i < 40) {
                ma40 = ma40Sum / (i + 1)
            } else {
                ma40Sum -= dataList[i - 40].cr?.cr ?: 0.0
                ma40 = ma40Sum / 40
            }
            ma40List.add(ma40)

            if (i < 60) {
                ma60 = ma60Sum / (i + 1)
            } else {
                ma60Sum -= dataList[i - 60].cr?.cr ?: 0.0
                ma60 = ma60Sum / 60
            }
            ma60List.add(ma60)

            ma1 = if (i < 5) {
                ma10List[0]
            } else {
                ma10List[i - 5]
            }

            ma2 = if (i < 9) {
                ma20List[0]
            } else {
                ma20List[i - 9]
            }

            ma3 = if (i < 17) {
                ma40List[0]
            } else {
                ma40List[i - 17]
            }

            ma4 = if (i < 25) {
                ma60List[0]
            } else {
                ma60List[i - 25]
            }

            dataList[i].cr = CrModel(cr, ma1, ma2, ma3, ma4)
        }
    }

    /**
     * 计算psy
     *
     * PSY：参数是12。公式：PSY=N日内的上涨天数/N×100%。
     *
     * @param dataList MutableList<KLineModel>
     * @return MutableList<KLineModel>
     */
    fun calcPsy(dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        var psy = 0.0
        var upDay = 0.0
        return calc(dataList) { i ->
            if (i > 0) {
                upDay += (if (dataList[i].closePrice - dataList[i - 1].closePrice > 0.0)  1.0 else 0.0)
                if (i < 12) {
                    psy = upDay / (i + 1) * 100
                } else {
                    if (i > 12) {
                        upDay -= (if (dataList[i - 11].closePrice - dataList[i - 12].closePrice > 0) 1.0 else 0.0)
                    }
                    psy = upDay / 12 * 100
                }
            }
            dataList[i].psy = PsyModel(psy)
        }
    }

    /**
     * 计算dma
     *
     * 参数是10、50、10。公式：DIF:MA(CLOSE,N1)-MA(CLOSE,N2);DIFMA:MA(DIF,M)
     *
     * @param dataList MutableList<KLineModel>
     * @return MutableList<KLineModel>
     */
    fun calcDma(dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        var dif: Double
        var difMa: Double
        var ma10s = 0.0
        var ma10: Double
        var ma50s = 0.0
        var ma50: Double
        var dif10s = 0.0

        return calc(dataList) { i ->
            val closePrice = dataList[i].closePrice

            ma10s += closePrice
            ma50s += closePrice

            if (i < 10) {
                ma10 = ma10s / (i + 1)
            } else {
                ma10s -= dataList[i - 10].closePrice
                ma10 = ma10s / 10
            }

            if (i < 50) {
                ma50 = ma50s / (i + 1)
            } else {
                ma50s -= dataList[i - 50].closePrice
                ma50 = ma50s / 50
            }
            dif = ma10 - ma50
            dif10s += dif

            if (i < 10) {
                difMa = dif10s / (i + 1)
            } else {
                dif10s -= dataList[i - 10].dma?.dif ?: 0.0
                difMa = dif10s / 10
            }

            dataList[i].dma = DmaModel(dif, difMa)
        }
    }

    /**
     * 计算trix
     *
     * TR=收盘价的N日指数移动平均的N日指数移动平均的N日指数移动平均；
     * TRIX=(TR-昨日TR)/昨日TR*100；
     * MATRIX=TRIX的M日简单移动平均；
     * 参数N设为12，参数M设为20；
     * 参数12、20
     * 公式：MTR:=EMA(EMA(EMA(CLOSE,N),N),N)
     * TRIX:(MTR-REF(MTR,1))/REF(MTR,1)*100;
     * TRMA:MA(TRIX,M)
     *
     * @param dataList MutableList<KLineModel>
     * @return MutableList<KLineModel>
     */
    fun calcTrix(dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        var trix = 0.0
        var maTrix: Double
        var sumTrix = 0.0

        var emaClose1: Double
        var oldEmaClose1 = 0.0

        var emaClose2: Double
        var oldEmaClose2 = 0.0

        var emaClose3: Double
        var oldEmaClose3 = 0.0
        val emaClose3List = mutableListOf<Double>()

        return calc(dataList) { i ->
            val closePrice = dataList[i].closePrice
            if (i == 0) {
                emaClose1 = closePrice
                emaClose2 = emaClose1
                emaClose3 = emaClose2
            } else {
                emaClose1 = (2 * closePrice + 11 * oldEmaClose1) / 13f
                emaClose2 = (2 * emaClose1 + 11 * oldEmaClose2) / 13f
                emaClose3 = (2 * emaClose2 + 11 * oldEmaClose3) / 13f
                val refEmaClose3 = emaClose3List[i - 1]
                trix = if (refEmaClose3 == 0.0) 0.0 else (emaClose3 - refEmaClose3) / refEmaClose3 * 100
            }
            oldEmaClose1 = emaClose1
            oldEmaClose2 = emaClose2
            oldEmaClose3 = emaClose3
            emaClose3List.add(emaClose3)
            sumTrix += trix
            if (i < 20) {
                maTrix = sumTrix / (i + 1)
            } else {
                sumTrix -= (dataList[i - 20].trix?.trix ?: 0.0)
                maTrix = sumTrix / 20
            }
            dataList[i].trix = TrixModel(trix, maTrix)
        }
    }

    /**
     * 计算obv指标
     *
     * VA:=IF(CLOSE>REF(CLOSE,1),VOL,-VOL);
     * OBV:SUM(IF(CLOSE=REF(CLOSE,1),0,VA),0);
     * MAOBV:MA(OBV,M);
     * VA赋值:如果收盘价>昨收,返回成交量(手),否则返回-成交量(手)
     * 输出OBV:如果收盘价=昨收,返回0,否则返回VA的历史累和
     * 输出MAOBV:OBV的M日简单移动平均
     *
     * 参数30
     *
     * @param dataList MutableList<KLineModel>
     * @return MutableList<KLineModel>
     */
    fun calcObv(dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        var obv: Double
        var sumObv = 0.0
        var maObv: Double
        var sumVa = 0.0
        return calc(dataList) { i ->
            val volume = dataList[i].volume
            if (i == 0) {
                obv = volume
                sumVa += volume
            } else {
                val refClosePrice = dataList[i - 1].closePrice
                val closePrice = dataList[i].closePrice
                val va = if (closePrice > refClosePrice) volume else -volume

                sumVa += va
                obv = if (closePrice == refClosePrice) 0.0 else sumVa
            }
            sumObv += obv
            if (i < 30) {
                maObv = sumObv / (i + 1)
            } else {
                sumObv -= (dataList[i - 30].obv?.obv ?: 0.0)
                maObv = sumObv / 30
            }

            dataList[i].obv = ObvModel(obv, maObv)
        }
    }

    /**
     * 计算vr指标
     *
     * 默认参数24 ， 30
     * VR=（AVS+1/2CVS）/（BVS+1/2CVS）
     * 24天以来凡是股价上涨那一天的成交量都称为AV，将24天内的AV总和相加后称为AVS
     * 24天以来凡是股价下跌那一天的成交量都称为BV，将24天内的BV总和相加后称为BVS
     * 24天以来凡是股价不涨不跌，则那一天的成交量都称为CV，将24天内的CV总和相加后称为CVS
     *
     * @param dataList MutableList<KLineModel>
     * @return MutableList<KLineModel>
     */
    fun calcVr(dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        var avs = 0.0
        var bvs = 0.0
        var cvs = 0.0
        var vr = 0.0
        var maVr: Double
        var sumVr = 0.0

        return calc(dataList) { i ->
            val closePrice = dataList[i].closePrice
            val openPrice = dataList[i].openPrice
            val volume = dataList[i].volume
            when {
                closePrice > openPrice -> avs += volume
                closePrice < openPrice -> bvs += volume
                else -> cvs += volume
            }

            if (i > 23) {
                val agoClosePrice = dataList[i - 24].closePrice
                val agoOpenPrice = dataList[i - 24].openPrice
                val agoVolume = dataList[i - 24].volume
                when {
                    agoClosePrice > agoOpenPrice -> avs -= agoVolume
                    agoClosePrice < agoOpenPrice -> bvs -= agoVolume
                    else -> cvs += agoVolume
                }
            }

            val v = bvs + 1.0 / 2.0 * cvs
            if (v != 0.0) {
                vr = (avs + 1.0 / 2.0 * cvs) / v * 100
            }
            sumVr += vr
            if (i < 30) {
                maVr = sumVr / (i + 1)
            } else {
                sumVr -= dataList[i - 30].vr?.vr ?: 0.0
                maVr = sumVr / 30
            }

            dataList[i].vr = VrModel(vr, maVr)
        }
    }

    /**
     * 计算wr指标
     *
     * 默认参数13 34 89
     * 公式 WR(N) = 100 * [ HIGH(N)-C ] / [ HIGH(N)-LOW(N) ]
     *
     * @param dataList MutableList<KLineModel>
     * @return MutableList<KLineModel>
     */
    fun calcWr(dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        var wr1: Double
        var wr2: Double
        var wr3: Double
        var h1 = Double.NEGATIVE_INFINITY
        var l1 = Double.POSITIVE_INFINITY
        var h2 = Double.NEGATIVE_INFINITY
        var l2 = Double.POSITIVE_INFINITY
        var h3 = Double.NEGATIVE_INFINITY
        var l3 = Double.POSITIVE_INFINITY

        var hl1: Double
        var hl2: Double
        var hl3: Double
        return calc(dataList) { i ->
            val closePrice = dataList[i].closePrice
            val highPrice = dataList[i].highPrice
            val lowPrice = dataList[i].lowPrice
            if (i < 13) {
                h1 = max(highPrice, h1)
                l1 = min(lowPrice, l1)
            } else {
                val highLowPriceArray = getHighLow(dataList.subList(i - 13, i))
                h1 = highLowPriceArray[0]
                l1 = highLowPriceArray[1]
            }
            hl1 = h1 - l1
            wr1 = if (hl1 != 0.0) {
                (h1 - closePrice) / hl1 * 100
            } else {
                0.0
            }

            if (i < 34) {
                h2 = max(highPrice, h2)
                l2 = min(lowPrice, l2)
            } else {
                val highLowPriceArray = getHighLow(dataList.subList(i - 34, i))
                h2 = highLowPriceArray[0]
                l2 = highLowPriceArray[1]
            }
            hl2 = h2 - l2
            wr2 = if (hl2 != 0.0) {
                (h2 - closePrice) / hl2 * 100
            } else {
                0.0
            }

            if (i < 89) {
                h3 = max(highPrice, h3)
                l3 = min(lowPrice, l3)
            } else {
                val highLowPriceArray = getHighLow(dataList.subList(i - 89, i))
                h3 = highLowPriceArray[0]
                l3 = highLowPriceArray[1]
            }
            hl3 = h3 - l3
            wr3 = if (hl3 != 0.0) {
                (h3 - closePrice) / hl3 * 100
            } else {
                0.0
            }

            dataList[i].wr = WrModel(wr1, wr2, wr3)
        }
    }

    /**
     * 计算mtm指标
     *
     * 默认参数6 10
     * 公式 MTM（N日）=C－CN
     *
     * @param dataList MutableList<KLineModel>
     * @return MutableList<KLineModel>
     */
    fun calcMtm(dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        var mtm: Double
        var mtmSum = 0.0
        var mtmMa: Double

        return calc(dataList) { i ->
            if (i < 6) {
                mtm = 0.0
                mtmMa = 0.0
            } else {
                val closePrice = dataList[i].closePrice
                mtm = closePrice - dataList[i - 6].closePrice
                mtmSum += mtm
                if (i < 16) {
                    mtmMa = mtmSum / (i - 6 + 1)
                } else {
                    mtmMa = mtmSum / 10
                    mtmSum -= dataList[i - 10].mtm?.mtm ?: 0.0
                }
            }
            dataList[i].mtm = MtmModel(mtm, mtmMa)
        }
    }

    /**
     * 简易波动指标
     * 默认参数N为14，参数M为9
     * 公式：
     * A=（今日最高+今日最低）/2
     * B=（前日最高+前日最低）/2
     * C=今日最高-今日最低
     * EM=（A-B）*C/今日成交额
     * EMV=N日内EM的累和
     * MAEMV=EMV的M日的简单移动平均
     *
     * @param dataList MutableList<KLineModel>
     * @return MutableList<KLineModel>
     */
    fun calcEmv(dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        var emv = 0.0
        var maEmv: Double
        var sumEmv = 0.0
        var em = 0.0
        val emList = mutableListOf<Double>()

        return calc(dataList) { i ->
            if (i > 0) {
                val highestPrice = dataList[i].highPrice
                val lowestPrice = dataList[i].lowPrice
                val preHighestPrice = dataList[i - 1].highPrice
                val preLowestPrice = dataList[i - 1].lowPrice
                val highSubLow = highestPrice - lowestPrice
                val halfHighAddLow = (highestPrice + lowestPrice) / 2
                val preHalfHighAddLow = (preHighestPrice + preLowestPrice) / 2
                em = (halfHighAddLow - preHalfHighAddLow) * highSubLow / dataList[i].turnover
            }
            emList.add(em)
            if (i < 14) {
                emv += em
            } else {
                emv -= emList[i - 14]
            }
            sumEmv += emv
            if (i < 9) {
                maEmv = sumEmv / (i + 1)
            } else {
                sumEmv -= dataList[i - 9].emv?.emv ?: 0.0
                maEmv = sumEmv / 9
            }
            dataList[i].emv = EmvModel(emv, maEmv)
        }
    }

    /**
     * 计算sar
     *
     * @param dataList MutableList<KLineModel>
     * @return MutableList<KLineModel>
     */
    fun calcSar(dataList: MutableList<KLineModel>): MutableList<KLineModel> {
        //加速因子
        var af = 0.0
        //极值
        var ep = -100.0
        //判断是上涨还是下跌  false：下跌
        var isIncreasing = false
        var sar = 0.0

        return calc(dataList) { i ->
            //上一个周期的sar
            val preSar = sar
            val highestPrice = dataList[i].highPrice
            val lowestPrice = dataList[i].lowPrice
            if (isIncreasing) {
                //上涨
                if (ep == -100.0 || ep < highestPrice) {
                    //重新初始化值
                    ep = highestPrice
                    af = min(af + 0.02, 2.0)
                }
                sar = preSar + af * (ep - preSar)
                val lowestPriceMin = min(dataList[max(1, i) - 1].lowPrice, lowestPrice)
                if (sar > dataList[i].lowPrice) {
                    sar = ep
                    //重新初始化值
                    af = 0.0
                    ep = -100.0
                    isIncreasing = !isIncreasing

                } else if (sar > lowestPriceMin) {
                    sar = lowestPriceMin
                }
            } else {
                if (ep == -100.0 || ep > lowestPrice) {
                    //重新初始化值
                    ep = lowestPrice
                    af = min(af + 0.02, 0.2)
                }
                sar = preSar + af * (ep - preSar)
                val highestPriceMax = max(dataList[max(1, i) - 1].lowPrice, highestPrice)
                if (sar < dataList[i].highPrice) {
                    sar = ep
                    //重新初始化值
                    af = 0.0
                    ep = -100.0
                    isIncreasing = !isIncreasing

                } else if (sar < highestPriceMax) {
                    sar = highestPriceMax
                }
            }

            dataList[i].sar = SarModel(sar)
        }
    }


    private inline fun calc(dataList: MutableList<KLineModel>, calcIndicator: (index: Int) -> Unit): MutableList<KLineModel> {
        var totalTurnover = 0.0
        var totalVolume = 0.0

        val dataSize = dataList.size
        for (i in 0 until dataSize) {
            val data = dataList[i]
            totalVolume += data.volume
            totalTurnover += data.turnover
            if (totalVolume != 0.0) {
                data.averagePrice = totalTurnover / totalVolume
            }
            calcIndicator(i)
        }
        return dataList
    }

    /**
     * 计算布林指标中的标准差
     * @param list MutableList<KLineModel>
     * @param ma Double
     * @return Double
     */
    private fun getBollMd(list: MutableList<KLineModel>, ma: Double): Double {
        val size = list.size
        var sum = 0.0
        for (i in 0 until size) {
            val closeMa = list[i].closePrice - ma
            sum += closeMa * closeMa
        }
        val b = sum > 0
        sum = abs(sum)
        val md = sqrt(sum / size)

        return if (b)  md else -1 * md
    }

    /**
     * 获取list中的最大的最高价
     *
     * @param list
     * @return
     */
    private fun getHigh(list: MutableList<KLineModel>): Double {
        var high = 0.0
        val size = list.size
        if (size > 0) {
            high = list[0].highPrice
            for (i in 0 until size) {
                high = max(list[i].highPrice, high)
            }
        }
        return high
    }

    /**
     * 获取list中的最小的最低价
     *
     * @param list
     * @return
     */
    private fun getLow(list: MutableList<KLineModel>): Double {
        var low = 0.0
        val size = list.size
        if (size > 0) {
            low = list[0].lowPrice
            for (i in 0 until size) {
                low = min(list[i].lowPrice, low)
            }
        }
        return low
    }

    /**
     * 获取最高最低价
     * @param list MutableList<KLineModel>
     * @return DoubleArray
     */
    private fun getHighLow(list: MutableList<KLineModel>): DoubleArray {
        var high = 0.0
        var low = 0.0
        val size = list.size
        if (size > 0) {
            high = list[0].highPrice
            low = list[0].lowPrice
            for (i in 0 until size) {
                high = max(list[i].highPrice, high)
                low = min(list[i].lowPrice, low)
            }
        }
        return doubleArrayOf(high, low)
    }
}