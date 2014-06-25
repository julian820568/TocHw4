//蔣承儒, F7400649
/*
讀取網頁資料後
首先確認是否match到"大道","路","街","巷"
	有的話，再確認有沒有重複到路名
		沒有->紀錄
		有->確認是否重複月份
			沒有->紀錄
			有->跳出迴圈讀下一行資料
*/

import java.io.*;
import java.net.*;

public class TocHw4 {
	static URL url;
	
	public static void main(String[] args) throws IOException {
		String input = "", road_name = "", year = "", price = "";
		String[][] road_month = new String[1000][40];
		int roadNum = 0;
		int[] monNum = new int[1000];
		int[][] disMonth_high_lowMoney = new int[1000][3];
		int head, tail;
		int i, j;
		Boolean matchRoad, matchYear;
		int maxDistinct = 0;
		int[] ans = new int[10];
		
		if( args.length == 1 )
			url = new URL(args[0]);
		else {
			System.out.println("reject");
			return;
		}
		
		URLConnection connect = url.openConnection();
		InputStreamReader isr = new 
				InputStreamReader(connect.getInputStream(), "UTF-8");
		BufferedReader buf = new BufferedReader(isr);
		
		for( i = 0; i < 1000; i++ )
			monNum[i] = 0;
		for( i = 0; i < 1000; i++ )
			for( j = 0; j < 3; j++ )
				disMonth_high_lowMoney[i][j] = 0;
		while( (input = buf.readLine()) != null ) {
			if( input.contains("土地區段位置或建物區門牌") ) {
				//土地區段位置或建物區門牌
				head = input.indexOf("土地區段位置或建物區門牌");
				tail = input.indexOf("土地移轉總面積平方公尺");
				road_name = input.substring( head+15, tail-3 );
				if( road_name.contains("大道") ) {
					tail = road_name.indexOf("大道");
					road_name = road_name.substring( 0, tail+2 );
				} else if( road_name.contains("路") ) {
					tail = road_name.indexOf("路");
					road_name = road_name.substring( 0, tail+1 );
				} else if( road_name.contains("街") ) {
					tail = road_name.indexOf("街");
					road_name = road_name.substring( 0, tail+1 );
				} else if( road_name.contains("巷") ) {
					tail = road_name.indexOf("巷");
					road_name = road_name.substring( 0, tail+1 );
				} else continue;
				//交易年月
				head = input.indexOf("交易年月");
				tail = input.indexOf("交易筆棟數");
				year = input.substring( head+6, tail-2 );
				//總價元
				head = input.indexOf("總價元");
				tail = input.indexOf("單價每平方公尺");
				price = input.substring( head+5, tail-2 );
				
				matchRoad = false;
				//try to match road name
				for( i = 0; i < roadNum; i++ ) {
					if( road_month[i][0].equals(road_name) ) {
						matchRoad = true;
						//road name matched, record price
						disMonth_high_lowMoney[i][1] = Math.max( 
								disMonth_high_lowMoney[i][1], Integer.valueOf(price) );
						disMonth_high_lowMoney[i][2] = Math.min( 
								disMonth_high_lowMoney[i][2], Integer.valueOf(price) );
						
						matchYear = false;
						//road name matched, try to match month
						for( j = 0; j < monNum[i]; j++ )
							if( road_month[i][j+1].equals(year) ) {
								matchYear = true;
								j = monNum[i];//break
							}
						
						if( matchYear == false ) {
							disMonth_high_lowMoney[i][0]++;
							road_month[i][++monNum[i]] = year;
							//System.out.println( year+road_month[i][0]+" 年"+monNum[i] );
						}
					}
						
				}
				if( matchRoad == false ) {
					road_month[roadNum++][0] = road_name;
					//the first time to record price
					road_month[i][++monNum[i]] = year;
					disMonth_high_lowMoney[i][0]++;
					disMonth_high_lowMoney[i][1] = Integer.valueOf(price);
					disMonth_high_lowMoney[i][2] = Integer.valueOf(price);
				}
			}
		}//end of record
		
		for( i = 0; i < roadNum; i++ )
			if( monNum[i] > maxDistinct ) {
				maxDistinct = monNum[i];
				ans[0] = i;
			}
		j = 1;
		for( i = 0; i < roadNum; i++ )
			if( monNum[i] == monNum[ans[0]] && ans[0] != i)
				ans[j++] = i;
		
		for( i = 0; i < j; i++ )
			System.out.println("\""+road_month[ans[i]][0]+", "+"最高成交價: "+
				disMonth_high_lowMoney[ans[i]][1]+", 最低成交價: "+
				disMonth_high_lowMoney[ans[i]][2]+"\"");
	}
}
