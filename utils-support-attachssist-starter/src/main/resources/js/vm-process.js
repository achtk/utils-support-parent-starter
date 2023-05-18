$('#exampleTableColumns').bootstrapTable({
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
        title: '序号',
        align: "center",
        formatter: function (value, row, index) {
            return index + 1;
        }
    }, {
        field: 'id',
        title: 'PID'
    }, {
        field: 'type',
        title: '用户'
    }, {
        field: 'threadName',
        title: '进程'
    }, {
        field: 'ex',
        title: '内存'
    }]
});
$('#exampleTableColumns1').bootstrapTable({
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
        title: '序号',
        align: "center",
        formatter: function (value, row, index) {
            return index + 1;
        }
    }, {
        field: 'id',
        title: 'PID'
    }, {
        field: 'type',
        title: '用户'
    }, {
        field: 'threadName',
        title: '进程'
    }, {
        field: 'ex',
        title: 'CPU占有率'
    }]
});