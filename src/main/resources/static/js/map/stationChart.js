
function initStationChart2() {
	var domChart = document.getElementById('div-station-chart');
	stationStateChart = echarts.init(domChart, 'dark');
	option = {
		// 全局调色盘。#28a745(success)
		color : [ '#dc3545', '#FF7F00', '#5CACEE', '#71C671' ],
		title : {
			subtext : '站点状态'
		},
		legend : {
			itemWidth : 10,
			itemHeight : 10,
			itemGap : 5,
			y : 'bottom',
			data : [ '异常', '离线', '正常', '未配置' ]
		},
		grid : {
			top : '20%',
			bottom : '13%',
			containLabel : true
		},
		xAxis : {
			type : 'value',
			minInterval: 1,
			boundaryGap : [ 0, 0.01 ]
		},
		yAxis : {
			type : 'category'
		},
		series : [ {
			name : '异常',
			type : 'bar',
			label : {
				normal : {
					show : true,
					position : 'inside'
				}
			},
			data : [ 0 ]
		}, {
			name : '离线',
			type : 'bar',
			label : {
				normal : {
					show : true,
					position : 'inside'
				}
			},
			data : [ 0 ]
		}, {
			name : '未配置',
			type : 'bar',
			label : {
				normal : {
					show : true,
					position : 'inside'
				}
			},
			data : [ 0 ]
		}, {
			name : '正常',
			type : 'bar',
			label : {
				normal : {
					show : true,
					position : 'inside'
				}
			},
			data : [ 0 ]
		} ]

	};
	stationStateChart.setOption(option);
}

function setStationChartData(dataValue) {

	var option = {
		series : [ {
			data : [ dataValue[0] ]
		}, {
			data : [ dataValue[1] ]
		}, {
			data : [ dataValue[2] ]
		}, {
			data : [ dataValue[3] ]
		} ]
	};
	stationStateChart.setOption(option);
}