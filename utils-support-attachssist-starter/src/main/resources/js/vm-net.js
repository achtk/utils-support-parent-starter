
function sizeTostr(size) {
    var data = "";
    if (size < 1 * 1024) { //如果小于0.1KB转化成B
        data = size.toFixed(2) + "B";
    } else if (size < 1 * 1024 * 1024) {//如果小于0.1MB转化成KB
        data = (size / 1024).toFixed(2) + "KB";
    } else if (size < 1 * 1024 * 1024 * 1024) { //如果小于0.1GB转化成MB
        data = (size / (1024 * 1024)).toFixed(2) + "MB";
    } else { //其他转化成GB
        data = (size / (1024 * 1024 * 1024)).toFixed(2) + "GB";
    }
    var sizestr = data + "";
    var len = sizestr.indexOf("\.");
    var dec = sizestr.substr(len + 1, 2);
    if (dec == "00") {//当小数点后为00时 去掉小数部分
        return sizestr.substring(0, len) + sizestr.substr(len + 3, 2);
    }
    return sizestr;
}
const net = new Area('container1', {
    data: [],
    xField: 'x',
    yField: 'y',
    seriesField: 'name',
    xAxis: {
        type: 'time',
        mask: 'HH:mm:ss',
    },
    smooth: true,
    label: {},
    point: {
        size: 5,
        shape: 'diamond',
        style: {
            fill: 'white',
            stroke: '#5B8FF9',
            lineWidth: 2,
        },
    },
    tooltip: {
        showMarkers: false,
        enterable: true,
        domStyles: {
            'g2-tooltip': {
                width: '150px',
                padding: 0,
            },
        },
        customContent: (title, items) => {
            let el = '';
            for (const index in items) {
                const data = items[index]?.data || {};
                const size = sizeTostr(data['y']);
                const tempDom = `<div class = "custom-tooltip-value">${data.name}<div class = "custom-tooltip-temp"><span>速度</span><span>${size}</span></div></div>`;

                el += `<div style="width: 200px" class="background-image">${tempDom}</div>`;
            }
            return el;
        },
    },
    state: {
        active: {
            style: {
                shadowBlur: 4,
                stroke: '#000',
                fill: 'red',
            },
        },
    },
    interactions: [{type: 'marker-active'}],
});

net.render();