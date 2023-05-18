/*! XmlBeautify v1.1.0 Copyright (c) 2019-2021 https://github.com/riversun(riversun.org@gmail.com) */
!function(e,n){"object"==typeof exports&&"object"==typeof module?module.exports=n():"function"==typeof define&&define.amd?define([],n):"object"==typeof exports?exports.XmlBeautify=n():e.XmlBeautify=n()}(this,(function(){return function(e){var n={};function t(r){if(n[r])return n[r].exports;var l=n[r]={i:r,l:!1,exports:{}};return e[r].call(l.exports,l,l.exports,t),l.l=!0,l.exports}return t.m=e,t.c=n,t.d=function(e,n,r){t.o(e,n)||Object.defineProperty(e,n,{enumerable:!0,get:r})},t.r=function(e){"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},t.t=function(e,n){if(1&n&&(e=t(e)),8&n)return e;if(4&n&&"object"==typeof e&&e&&e.__esModule)return e;var r=Object.create(null);if(t.r(r),Object.defineProperty(r,"default",{enumerable:!0,value:e}),2&n&&"string"!=typeof e)for(var l in e)t.d(r,l,function(n){return e[n]}.bind(null,l));return r},t.n=function(e){var n=e&&e.__esModule?function(){return e.default}:function(){return e};return t.d(n,"a",n),n},t.o=function(e,n){return Object.prototype.hasOwnProperty.call(e,n)},t.p="/",t(t.s=0)}([function(e,n,t){e.exports=t(1)},function(e,n,t){"use strict";function r(e,n){for(var t=0;t<n.length;t++){var r=n[t];r.enumerable=r.enumerable||!1,r.configurable=!0,"value"in r&&(r.writable=!0),Object.defineProperty(e,r.key,r)}}t.r(n),t.d(n,"default",(function(){return l}));var l=function(){function e(n){!function(e,n){if(!(e instanceof n))throw new TypeError("Cannot call a class as a function")}(this,e);var t=n||{};this.userExternalParser=!1,t.parser?(this.userExternalParser=!0,this.parser=new t.parser):this.parser=new DOMParser}var n,t,l;return n=e,(t=[{key:"hasXmlDef",value:function(e){return e.indexOf("<?xml")>=0}},{key:"getEncoding",value:function(e){if(!this.hasXmlDef(e))return null;var n=e.toLowerCase().indexOf('encoding="')+'encoding="'.length,t=e.indexOf('"?>');return e.substr(n,t-n)}},{key:"_children",value:function(e){for(var n=[],t=e.childNodes.length,r=0;r<t;r++)1===e.childNodes[r].nodeType&&n.push(e.childNodes[r]);return n}},{key:"beautify",value:function(e,n){var t=this,r=t.parser.parseFromString(e,"text/xml"),l="  ",a=!1;n&&(n.indent&&(l=n.indent),1==n.useSelfClosingElement&&(a=n.useSelfClosingElement));var i=null;t.hasXmlDef(e)&&(i='<?xml version="1.0" encoding="'+t.getEncoding(e)+'"?>');var o={indentText:l,xmlText:"",useSelfClosingElement:a,indentLevel:0};t.userExternalParser?t._parseInternally(this._children(r)[0],o):t._parseInternally(r.children[0],o);var u="";return i&&(u+=i+"\n"),u+=o.xmlText}},{key:"_parseInternally",value:function(e,n){var t,r=this,l=e.textContent;0==l.replace(/ /g,"").replace(/\r?\n/g,"").replace(/\n/g,"").replace(/\t/g,"").length&&(l=""),t=r.userExternalParser?!(r._children(e).length>0):!(e.children.length>0);var a=l&&l.length>0,i=t&&a,o=t&&!a,u=n.useSelfClosingElement,s="";i&&(s=l);for(var f,c="",d=0;d<n.indentLevel;d++)c+=n.indentText;n.xmlText+=c,n.xmlText+="<"+e.tagName;for(var x=0;x<e.attributes.length;x++){var p=e.attributes[x];n.xmlText+=" "+p.name+'="'+p.textContent+'"'}n.xmlText+=o&&u?" />":">",i?n.xmlText+=s:o&&!u||(n.xmlText+="\n"),n.indentLevel++,f=r.userExternalParser?r._children(e).length:e.children.length;for(var m=0;m<f;m++){var v=void 0;v=r.userExternalParser?r._children(e)[m]:e.children[m],r._parseInternally(v,n)}if(n.indentLevel--,o)if(u);else{var h="</"+e.tagName+">";n.xmlText+=h,n.xmlText+="\n"}else{var g="</"+e.tagName+">";t&&a||(n.xmlText+=c),n.xmlText+=g,n.xmlText+="\n"}}}])&&r(n.prototype,t),l&&r(n,l),e}()}]).default}));