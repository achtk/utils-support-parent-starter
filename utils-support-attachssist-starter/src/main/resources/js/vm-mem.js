const mem = new Pie('mem', {
    appendPadding: 10,
    data: [],
    angleField: 'y',
    colorField: 'x',
    radius: 0.75,
    label: {
        type: 'spider',
        labelHeight: 28,
        content: '{name}\n{percentage}',
    },
    interactions: [{type: 'element-selected'}, {type: 'element-active'}],

});

mem.render();