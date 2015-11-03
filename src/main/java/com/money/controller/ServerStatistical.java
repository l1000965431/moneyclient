package com.money.controller;

import com.money.Service.ServerStatisticalService.ServerStatisticalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by seele on 2015/11/2.
 */

@Controller
@RequestMapping("/ServerStatistical")
public class ServerStatistical extends ControllerBase implements IController {
    @Autowired
    ServerStatisticalService serverStatisticalService;

    /**
     * 每日投资人总收益
     * @return
     */
    @RequestMapping("/TotalLotteryEveryDay")
    @ResponseBody
    public int getTotalLotteryEveryDay(HttpServletRequest httpServletRequest) {
        String Date = httpServletRequest.getParameter("Date");
        return serverStatisticalService.getTotlaLotterySum( Date );
    }

    /**
     * 一共累计总投资额度
     * @return
     */
    @RequestMapping("/TotalInvestment")
    @ResponseBody
    public int getTotalInvestment() {
        return serverStatisticalService.getTotalInvestment();
    }

    /**
     * 一共累计投资次数
     * @return
     */
    @RequestMapping("/TotalInvestmentNum")
    @ResponseBody
    public int getTotalInvestmentNum() {
        return serverStatisticalService.getTotalInvestmentNum();
    }

    /**
     * 一共给投资人带来多少收益
     * @return
     */
    @RequestMapping("/TotalLottery")
    @ResponseBody
    public int getTotalLottery() {
        return serverStatisticalService.getTotalLottery();
    }

    /**
     * 平均每人充值多少钱
     * @return
     */
    @RequestMapping("/AverageWallet")
    @ResponseBody
    public float getAverageWallet() {
        return serverStatisticalService.getAverageWallet();
    }

    /**
     * 每日公司营收金额
     * @return
     */
    @RequestMapping("/RevenueWallet")
    @ResponseBody
    public int getRevenueWallet( HttpServletRequest httpServletRequest ) {
        String Date = httpServletRequest.getParameter("Date");
        return serverStatisticalService.getRevenueWallet( Date );
    }

    /**
     * 每日提交的项目数
     * @return
     */
    @RequestMapping("/ActivityVerify")
    @ResponseBody
    public int getActivityVerify( HttpServletRequest httpServletRequest ) {
        String Date = httpServletRequest.getParameter("Date");
        return serverStatisticalService.getActivityVerify(Date);
    }
}
