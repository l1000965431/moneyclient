package com.money.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 全局设置数据结构
 * <p>User: Guo Hong
 * <p>Date: 15-7-8
 * <p>Version: 1.0
 */
public class GlobalConfigDataStruct extends BaseModel{
    /**
     * 总分成设置
     */
    public static class GlobalBonusProportion{
        private float investorProportion;
        private float projectProportion;
        private float ourProportion;

        public float getInvestorProportion() {
            return investorProportion;
        }

        public void setInvestorProportion(float investorProportion) {
            this.investorProportion = investorProportion;
        }

        public float getProjectProportion() {
            return projectProportion;
        }

        public void setProjectProportion(float projectProportion) {
            this.projectProportion = projectProportion;
        }

        public float getOurProportion() {
            return ourProportion;
        }

        public void setOurProportion(float ourProportion) {
            this.ourProportion = ourProportion;
        }
    }

    /**
     * 投资层次设置
     */
    public static class SRInvestLevel{
        public List<Integer> getInvestLevelList() {
            return investLevelList;
        }

        public void setInvestLevelList(List<Integer> investLevelList) {
            this.investLevelList = investLevelList;
        }

        List<Integer> investLevelList = new ArrayList<Integer>();
    }

    /**
     * 投资层次比例设置
     */
    public static class SRInvestProportion{
        public HashMap<Integer, ArrayList<Float>> getInvestProportion() {
            return investProportion;
        }

        public void setInvestProportion(HashMap<Integer, ArrayList<Float>> investProportion) {
            this.investProportion = investProportion;
        }

        HashMap<Integer, ArrayList<Float>> investProportion = new HashMap<Integer, ArrayList<Float>>();
    }

    /**
     * 收益层次设置
     */
    public static class SREarningLevel{
        List<Integer> earningLevelList = new ArrayList<Integer>();

        public List<Integer> getEarningLevelList() {
            return earningLevelList;
        }

        public void setEarningLevelList(List<Integer> earningLevelList) {
            this.earningLevelList = earningLevelList;
        }
    }

    /**
     * 收益层次比例设置
     */
    public static class SREarningProportion{
        HashMap<Integer, ArrayList<Float>> earningProportion = new HashMap<Integer, ArrayList<Float>>();

        public HashMap<Integer, ArrayList<Float>> getEarningProportion() {
            return earningProportion;
        }

        public void setEarningProportion(HashMap<Integer, ArrayList<Float>> earningProportion) {
            this.earningProportion = earningProportion;
        }
    }

    /**
     * 喊卡系数设置
     */
    public static class CutFactor{
        public int getInvestCutFactor() {
            return investCutFactor;
        }

        public void setInvestCutFactor(int investCutFactor) {
            this.investCutFactor = investCutFactor;
        }

        public int getEarningCutFactor() {
            return earningCutFactor;
        }

        public void setEarningCutFactor(int earningCutFactor) {
            this.earningCutFactor = earningCutFactor;
        }

        private int investCutFactor;
        private int earningCutFactor;
    }
}
