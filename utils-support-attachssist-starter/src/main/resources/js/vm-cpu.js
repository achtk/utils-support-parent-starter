const paletteSemanticRed = '#F4664A';
const brandColor = '#5B8FF9';
const cpu = new Area('container', {
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
    tooltip: {showMarkers: false},
    interactions: [{type: 'marker-active'}],
    color: ({value}) => {
        if (value < 0.05) {
            return paletteSemanticRed;
        }
        return brandColor;
    },
});

cpu.render();