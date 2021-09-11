package com.org.clarivate;

import java.util.List;

public class ConfigMapper {

	public static void main(String[] args) {
		System.out.println("Started");
		ZuulFilterMaps zfm = new ZuulFilterMaps("prod");
		KongConfigMaps kcm = new KongConfigMaps("prod");
		List<String>  zuulMapping = zfm.getZuulConfigFromZuulStats();
//		List<String>  zuulMapping = zfm.getZuulConfigFromZuulStats();
//		insertIntoExcel(zuulMapping, "zuulConfigFromZuulStats");
//		List<String>  zuulMapping = zfm.getZuulConfigFromJsonConfig("/Users/rakeshkingsley/Desktop/myProjectCode/compareZuulKong/prod");
//		List<String>  zuulMapping = zfm.getZuulConfigFromJsonConfig("/Users/rakeshkingsley/Desktop/myProjectCode/zuulConfig/prod");
//		List<String>  zuulMapping = zfm.getZuulConfigFromJsonConfig("/Users/rakeshkingsley/Desktop/myProjectCode/compareZuulKong/stable");
//		List<String>  zuulMapping = zfm.getZuulConfigFromJsonConfig("/Users/rakeshkingsley/Desktop/myProjectCode/compareZuulKong/perf");
		zuulMapping = SortHandler.getHorizontalSortedListByPosition(zuulMapping, 1);
		zuulMapping = SortHandler.getHorizontalSortedListByPosition(zuulMapping, 4);
//		zuulMapping = SortHandler.getHorizontalSortedListByPosition(zuulMapping, 8);
		FileHandler.insertIntoExcel(zuulMapping, "zuulProd2Config03082021");
//		FileHandler.insertIntoExcel(zuulMapping, "zuulSharedZuulStats");
//		insertIntoExcel(zuulMapping, "zuulStableConfig");
//		insertIntoExcel(zuulMapping, "zuulPerfConfig");
		List<String>  kongMapping = kcm.getKongConfig("/Users/rakeshkingsley/Desktop/myProjectCode/kongConfig/prod");
//		List<String>  kongMapping = kcm.getKongConfig("/Users/rakeshkingsley/Desktop/myProjectCode/kongConfig/prod");
//		List<String>  kongMapping = kcm.getKongConfig("/Users/rakeshkingsley/Desktop/myProjectCode/kongConfig/stable");
//		List<String>  kongMapping = kcm.getKongConfig("/Users/rakeshkingsley/Desktop/myProjectCode/kongConfig/perf");
//		kongMapping = SortHandler.getHorizontalSortedListByPosition(kongMapping, 3);
//		kongMapping = SortHandler.getHorizontalSortedListByPosition(kongMapping, 4);
//		kongMapping = SortHandler.getHorizontalSortedListByPosition(kongMapping, 6);
		FileHandler.insertIntoExcel(kongMapping, "kongProd2Config03082021");
//		insertIntoExcel(kongMapping, "kongProdConfig");
//		insertIntoExcel(kongMapping, "kongStableConfig");
//		insertIntoExcel(kongMapping, "kongPerfConfig");
		System.out.println(zuulMapping.get(0));
		System.out.println("Completed");
	}
	
	

}
