package com.money.Service.GlobalConifg;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.dao.GeneraDAO;
import com.money.model.GlobalConfigDataStruct;
import com.money.model.GlobalConfigModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 全局设置服务
 * <p>User: Guo Hong
 * <p>Date: 15-7-8
 * <p>Version: 1.0
**/
@Service("GlobalConfigService")
public class GlobalConfigService extends ServiceBase implements ServiceInterface {
    @Autowired
    private GeneraDAO generaDAO;

    /**
     * 总分成比例键
     */
    public static final String GLOBAL_BONUS = "GLOBAL_BONUS";
    private GlobalConfigDataStruct.GlobalBonusProportion globalBonusProportion = new GlobalConfigDataStruct.GlobalBonusProportion();

    /**
     * 小R投资层次分布
     * 值存储为JSON数组
     */
    public static final String SR_INVEST_LEVEL = "SR_INVEST_LEVEL";

    /**
     * 小R投资层次比例分布
     */
    public static final String SR_INVEST_PROPORTION = "SR_INVEST_PROPORTION";

    /**
     * 喊卡系数
     */
    public static final String CUT_FACTOR = "CUT_FACTOR";

    /**
     * 小R收益层次分布
     */
    public static final String SR_EARNING_LEVEL = "SR_EARNING_LEVEL";

    /**
     * 小R收益层次比例分布
     */
    public static final String SR_EARNING_PROPORTION = "SR_EARNING_PROPORTION";

    public void setValue(String key, String value){
        GlobalConfigModel configModel = getGlobalConfigByKey(key);

        if( configModel == null ){
            configModel = new GlobalConfigModel();
            configModel.setKey(key);
            generaDAO.save(configModel);
            return;
        }
        configModel.setValue(value);

        generaDAO.update(configModel);
    }

    public String getValue(String key){
        GlobalConfigModel configModel = getGlobalConfigByKey(key);
        if( configModel == null ){
            return "";
        }

        return configModel.getValue();
    }

    /**
     * 获得小R投资层次
     * @return
     */
    public List<Integer> getInvestLevelList(){
        String jsonString = getValue(SR_INVEST_LEVEL);
        if( jsonString.isEmpty() ){
            return new ArrayList<Integer>();
        }

        GlobalConfigDataStruct.SRInvestLevel srInvestLevel = GsonUntil.jsonToJavaClass(jsonString, GlobalConfigDataStruct.SRInvestLevel.class);
        return srInvestLevel.getInvestLevelList();
    }

    /**
     * 设置小R投资层次
     * @param investList
     */
    public void setInvestLevelList(List<Integer> investList){
        GlobalConfigDataStruct.SRInvestLevel srInvestLevel = new GlobalConfigDataStruct.SRInvestLevel();
        srInvestLevel.setInvestLevelList(investList);
        String jsonString = GsonUntil.JavaClassToJson(srInvestLevel);
        setValue(SR_INVEST_LEVEL, jsonString);
    }

    /**
     * 获得小R投资层次比例分布
     * @return
     */
    public HashMap<Integer, ArrayList<Float>> getInvestProportion(){
        String jsonString = getValue(SR_INVEST_PROPORTION);
        if(jsonString.isEmpty()){
            return new HashMap<Integer, ArrayList<Float>>();
        }

        GlobalConfigDataStruct.SRInvestProportion srInvestProportion = GsonUntil.jsonToJavaClass(jsonString, GlobalConfigDataStruct.SRInvestProportion.class);
        return srInvestProportion.getInvestProportion();
    }

    /**
     * 设置小R投资层次比例分布
     * @param investProportion
     */
    public void setInvestProportion(HashMap<Integer, ArrayList<Float>> investProportion){
        GlobalConfigDataStruct.SRInvestProportion srInvestProportion = new GlobalConfigDataStruct.SRInvestProportion();
        srInvestProportion.setInvestProportion(investProportion);
        String jsonString = GsonUntil.JavaClassToJson(srInvestProportion);
        setValue(SR_INVEST_PROPORTION, jsonString);
    }

    /**
     * 得到收益分布层次
     * @return
     */
    public List<Integer> getEarningLevelList(){
        String jsonString = getValue(SR_EARNING_LEVEL);
        if(jsonString.isEmpty()){
            return new ArrayList<Integer>();
        }

        GlobalConfigDataStruct.SREarningLevel srEarningLevel = GsonUntil.jsonToJavaClass(jsonString, GlobalConfigDataStruct.SREarningLevel.class);
        return srEarningLevel.getEarningLevelList();
    }

    /**
     * 设置收益层次
     */
    public void setEarningLevelList(List<Integer> earningLevelList){
        GlobalConfigDataStruct.SREarningLevel srEarningLevel = new GlobalConfigDataStruct.SREarningLevel();
        srEarningLevel.setEarningLevelList(earningLevelList);
        setValue(SR_EARNING_LEVEL, GsonUntil.JavaClassToJson(srEarningLevel));
    }

    /**
     * 获得收益层次分布
     */
    public HashMap<Integer,ArrayList<Float>> getEarningProportion(){
        String jsonString = getValue(SR_EARNING_PROPORTION);
        if(jsonString.isEmpty()){
            return new HashMap<Integer, ArrayList<Float>>();
        }

        GlobalConfigDataStruct.SREarningProportion srEarningProportion = new GlobalConfigDataStruct.SREarningProportion();
        return srEarningProportion.getEarningProportion();
    }

    /**
     * 设置收益层次分布
     */
    public void setEarningProportion(HashMap<Integer,ArrayList<Float>> earningProportion){
        GlobalConfigDataStruct.SREarningProportion srEarningProportion = new GlobalConfigDataStruct.SREarningProportion();
        srEarningProportion.setEarningProportion(earningProportion);
        setValue(SR_EARNING_PROPORTION, GsonUntil.JavaClassToJson(srEarningProportion));
    }

    /**
     * 得到喊卡系数
     * @return
     */
    public GlobalConfigDataStruct.CutFactor getCutFactors(){
        String jsonString = getValue(CUT_FACTOR);
        if(jsonString.isEmpty()){
            return null;
        }
        return GsonUntil.jsonToJavaClass(jsonString, GlobalConfigDataStruct.CutFactor.class);
    }

    /**
     * 设置喊卡系数
     * @param cutFactor
     */
    public void setCutFactors(GlobalConfigDataStruct.CutFactor cutFactor){
        setValue(CUT_FACTOR, GsonUntil.JavaClassToJson(cutFactor));
    }



    private GlobalConfigModel getGlobalConfigByKey(String key){
        return (GlobalConfigModel)generaDAO.load(GlobalConfigModel.class, key);
    }
}
