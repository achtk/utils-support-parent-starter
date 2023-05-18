const file = new Column('file', {
    data: [],
    xField: 'x',
    yField: 'y',
    seriesField: 'y',
    conversionTag: {},
    xAxis: {
        label: {
            autoHide: true,
            autoRotate: false,
        },
    },
    label: {
        // 可手动配置 label 数据标签位置
        position: 'top', // 'top', 'bottom', 'middle',
        // 配置样式
        style: {
            fill: '#FFFFFF',
            opacity: 0.6,
        },
    },
    color: ({value}) => {
        if (value < 0.05) {
            return paletteSemanticRed;
        }
        return brandColor;
    },
    legend: false,
});

file.render();
$('#file1').bootstrapTable({
    data: [],
    type: 'post',
    iconSize: 'outline',
    showColumns: true,
    pageSize: 20,                     //每页的记录行数（*）
    pageList: [20, 40, 60, 80],        //可供选择的每页的行数（*）
    pagination: true,                   //是否显示分页（*）
    icons: {
        refresh: 'glyphicon-repeat',
        toggle: 'glyphicon-list-alt',
        columns: 'glyphicon-list'
    },
    columns: [{
        field: 'threadName',
        title: '磁盘'
    }, {
        field: 'db',
        title: '剩余大小'
    }, {
        field: 'id',
        title: '总大小'
    }, {
        field: 'pid',
        title: '描述'
    }, {
        field: 'ex',
        title: '已使用',
        formatter: function (data) {
            return (parseFloat(data) * 100).toFixed(2) + "%";
        }
    }]
});