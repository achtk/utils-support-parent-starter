(function (global, factory) {
    typeof exports === 'object' && typeof module !== 'undefined' ? factory(require('jquery')) :
        typeof define === 'function' && define.amd ? define(['jquery'], factory) :
            (global = typeof globalThis !== 'undefined' ? globalThis : global || self, factory(global.jQuery));
})(this, (function ($$5) {
    'use strict';

    function _interopDefaultLegacy(e) {
        return e && typeof e === 'object' && 'default' in e ? e : {'default': e};
    }

    var $__default = /*#__PURE__*/_interopDefaultLegacy($$5);

    function _classCallCheck(instance, Constructor) {
        if (!(instance instanceof Constructor)) {
            throw new TypeError("Cannot call a class as a function");
        }
    }

    function _defineProperties(target, props) {
        for (var i = 0; i < props.length; i++) {
            var descriptor = props[i];
            descriptor.enumerable = descriptor.enumerable || false;
            descriptor.configurable = true;
            if ("value" in descriptor) descriptor.writable = true;
            Object.defineProperty(target, descriptor.key, descriptor);
        }
    }

    function _createClass(Constructor, protoProps, staticProps) {
        if (protoProps) _defineProperties(Constructor.prototype, protoProps);
        if (staticProps) _defineProperties(Constructor, staticProps);
        Object.defineProperty(Constructor, "prototype", {
            writable: false
        });
        return Constructor;
    }

    function _inherits(subClass, superClass) {
        if (typeof superClass !== "function" && superClass !== null) {
            throw new TypeError("Super expression must either be null or a function");
        }

        subClass.prototype = Object.create(superClass && superClass.prototype, {
            constructor: {
                value: subClass,
                writable: true,
                configurable: true
            }
        });
        Object.defineProperty(subClass, "prototype", {
            writable: false
        });
        if (superClass) _setPrototypeOf(subClass, superClass);
    }

    function _getPrototypeOf(o) {
        _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) {
            return o.__proto__ || Object.getPrototypeOf(o);
        };
        return _getPrototypeOf(o);
    }

    function _setPrototypeOf(o, p) {
        _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) {
            o.__proto__ = p;
            return o;
        };

        return _setPrototypeOf(o, p);
    }

    function _isNativeReflectConstruct() {
        if (typeof Reflect === "undefined" || !Reflect.construct) return false;
        if (Reflect.construct.sham) return false;
        if (typeof Proxy === "function") return true;

        try {
            Boolean.prototype.valueOf.call(Reflect.construct(Boolean, [], function () {
            }));
            return true;
        } catch (e) {
            return false;
        }
    }

    function _assertThisInitialized(self) {
        if (self === void 0) {
            throw new ReferenceError("this hasn't been initialised - super() hasn't been called");
        }

        return self;
    }

    function _possibleConstructorReturn(self, call) {
        if (call && (typeof call === "object" || typeof call === "function")) {
            return call;
        } else if (call !== void 0) {
            throw new TypeError("Derived constructors may only return object or undefined");
        }

        return _assertThisInitialized(self);
    }

    function _createSuper(Derived) {
        var hasNativeReflectConstruct = _isNativeReflectConstruct();

        return function _createSuperInternal() {
            var Super = _getPrototypeOf(Derived),
                result;

            if (hasNativeReflectConstruct) {
                var NewTarget = _getPrototypeOf(this).constructor;

                result = Reflect.construct(Super, arguments, NewTarget);
            } else {
                result = Super.apply(this, arguments);
            }

            return _possibleConstructorReturn(this, result);
        };
    }

    function _superPropBase(object, property) {
        while (!Object.prototype.hasOwnProperty.call(object, property)) {
            object = _getPrototypeOf(object);
            if (object === null) break;
        }

        return object;
    }

    function _get() {
        if (typeof Reflect !== "undefined" && Reflect.get) {
            _get = Reflect.get;
        } else {
            _get = function _get(target, property, receiver) {
                var base = _superPropBase(target, property);

                if (!base) return;
                var desc = Object.getOwnPropertyDescriptor(base, property);

                if (desc.get) {
                    return desc.get.call(arguments.length < 3 ? target : receiver);
                }

                return desc.value;
            };
        }

        return _get.apply(this, arguments);
    }

    function _slicedToArray(arr, i) {
        return _arrayWithHoles(arr) || _iterableToArrayLimit(arr, i) || _unsupportedIterableToArray(arr, i) || _nonIterableRest();
    }

    function _arrayWithHoles(arr) {
        if (Array.isArray(arr)) return arr;
    }

    function _iterableToArrayLimit(arr, i) {
        var _i = arr == null ? null : typeof Symbol !== "undefined" && arr[Symbol.iterator] || arr["@@iterator"];

        if (_i == null) return;
        var _arr = [];
        var _n = true;
        var _d = false;

        var _s, _e;

        try {
            for (_i = _i.call(arr); !(_n = (_s = _i.next()).done); _n = true) {
                _arr.push(_s.value);

                if (i && _arr.length === i) break;
            }
        } catch (err) {
            _d = true;
            _e = err;
        } finally {
            try {
                if (!_n && _i["return"] != null) _i["return"]();
            } finally {
                if (_d) throw _e;
            }
        }

        return _arr;
    }

    function _unsupportedIterableToArray(o, minLen) {
        if (!o) return;
        if (typeof o === "string") return _arrayLikeToArray(o, minLen);
        var n = Object.prototype.toString.call(o).slice(8, -1);
        if (n === "Object" && o.constructor) n = o.constructor.name;
        if (n === "Map" || n === "Set") return Array.from(o);
        if (n === "Arguments" || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n)) return _arrayLikeToArray(o, minLen);
    }

    function _arrayLikeToArray(arr, len) {
        if (len == null || len > arr.length) len = arr.length;

        for (var i = 0, arr2 = new Array(len); i < len; i++) arr2[i] = arr[i];

        return arr2;
    }

    function _nonIterableRest() {
        throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.");
    }

    var commonjsGlobal = typeof globalThis !== 'undefined' ? globalThis : typeof window !== 'undefined' ? window : typeof global !== 'undefined' ? global : typeof self !== 'undefined' ? self : {};

    var check = function (it) {
        return it && it.Math == Math && it;
    };

    // https://github.com/zloirock/core-js/issues/86#issuecomment-115759028
    var global$q =
        // eslint-disable-next-line es-x/no-global-this -- safe
        check(typeof globalThis == 'object' && globalThis) ||
        check(typeof window == 'object' && window) ||
        // eslint-disable-next-line no-restricted-globals -- safe
        check(typeof self == 'object' && self) ||
        check(typeof commonjsGlobal == 'object' && commonjsGlobal) ||
        // eslint-disable-next-line no-new-func -- fallback
        (function () {
            return this;
        })() || Function('return this')();

    var objectGetOwnPropertyDescriptor = {};

    var fails$c = function (exec) {
        try {
            return !!exec();
        } catch (error) {
            return true;
        }
    };

    var fails$b = fails$c;

    // Detect IE8's incomplete defineProperty implementation
    var descriptors = !fails$b(function () {
        // eslint-disable-next-line es-x/no-object-defineproperty -- required for testing
        return Object.defineProperty({}, 1, {
            get: function () {
                return 7;
            }
        })[1] != 7;
    });

    var fails$a = fails$c;

    var functionBindNative = !fails$a(function () {
        // eslint-disable-next-line es-x/no-function-prototype-bind -- safe
        var test = (function () { /* empty */
        }).bind();
        // eslint-disable-next-line no-prototype-builtins -- safe
        return typeof test != 'function' || test.hasOwnProperty('prototype');
    });

    var NATIVE_BIND$2 = functionBindNative;

    var call$4 = Function.prototype.call;

    var functionCall = NATIVE_BIND$2 ? call$4.bind(call$4) : function () {
        return call$4.apply(call$4, arguments);
    };

    var objectPropertyIsEnumerable = {};

    var $propertyIsEnumerable$1 = {}.propertyIsEnumerable;
    // eslint-disable-next-line es-x/no-object-getownpropertydescriptor -- safe
    var getOwnPropertyDescriptor$1 = Object.getOwnPropertyDescriptor;

    // Nashorn ~ JDK8 bug
    var NASHORN_BUG = getOwnPropertyDescriptor$1 && !$propertyIsEnumerable$1.call({1: 2}, 1);

    // `Object.prototype.propertyIsEnumerable` method implementation
    // https://tc39.es/ecma262/#sec-object.prototype.propertyisenumerable
    objectPropertyIsEnumerable.f = NASHORN_BUG ? function propertyIsEnumerable(V) {
        var descriptor = getOwnPropertyDescriptor$1(this, V);
        return !!descriptor && descriptor.enumerable;
    } : $propertyIsEnumerable$1;

    var createPropertyDescriptor$3 = function (bitmap, value) {
        return {
            enumerable: !(bitmap & 1),
            configurable: !(bitmap & 2),
            writable: !(bitmap & 4),
            value: value
        };
    };

    var NATIVE_BIND$1 = functionBindNative;

    var FunctionPrototype$1 = Function.prototype;
    var bind$2 = FunctionPrototype$1.bind;
    var call$3 = FunctionPrototype$1.call;
    var uncurryThis$f = NATIVE_BIND$1 && bind$2.bind(call$3, call$3);

    var functionUncurryThis = NATIVE_BIND$1 ? function (fn) {
        return fn && uncurryThis$f(fn);
    } : function (fn) {
        return fn && function () {
            return call$3.apply(fn, arguments);
        };
    };

    var uncurryThis$e = functionUncurryThis;

    var toString$5 = uncurryThis$e({}.toString);
    var stringSlice = uncurryThis$e(''.slice);

    var classofRaw$1 = function (it) {
        return stringSlice(toString$5(it), 8, -1);
    };

    var global$p = global$q;
    var uncurryThis$d = functionUncurryThis;
    var fails$9 = fails$c;
    var classof$5 = classofRaw$1;

    var Object$4 = global$p.Object;
    var split = uncurryThis$d(''.split);

    // fallback for non-array-like ES3 and non-enumerable old V8 strings
    var indexedObject = fails$9(function () {
        // throws an error in rhino, see https://github.com/mozilla/rhino/issues/346
        // eslint-disable-next-line no-prototype-builtins -- safe
        return !Object$4('z').propertyIsEnumerable(0);
    }) ? function (it) {
        return classof$5(it) == 'String' ? split(it, '') : Object$4(it);
    } : Object$4;

    var global$o = global$q;

    var TypeError$8 = global$o.TypeError;

    // `RequireObjectCoercible` abstract operation
    // https://tc39.es/ecma262/#sec-requireobjectcoercible
    var requireObjectCoercible$3 = function (it) {
        if (it == undefined) throw TypeError$8("Can't call method on " + it);
        return it;
    };

    // toObject with fallback for non-array-like ES3 strings
    var IndexedObject$1 = indexedObject;
    var requireObjectCoercible$2 = requireObjectCoercible$3;

    var toIndexedObject$5 = function (it) {
        return IndexedObject$1(requireObjectCoercible$2(it));
    };

    // `IsCallable` abstract operation
    // https://tc39.es/ecma262/#sec-iscallable
    var isCallable$c = function (argument) {
        return typeof argument == 'function';
    };

    var isCallable$b = isCallable$c;

    var isObject$7 = function (it) {
        return typeof it == 'object' ? it !== null : isCallable$b(it);
    };

    var global$n = global$q;
    var isCallable$a = isCallable$c;

    var aFunction = function (argument) {
        return isCallable$a(argument) ? argument : undefined;
    };

    var getBuiltIn$5 = function (namespace, method) {
        return arguments.length < 2 ? aFunction(global$n[namespace]) : global$n[namespace] && global$n[namespace][method];
    };

    var uncurryThis$c = functionUncurryThis;

    var objectIsPrototypeOf = uncurryThis$c({}.isPrototypeOf);

    var getBuiltIn$4 = getBuiltIn$5;

    var engineUserAgent = getBuiltIn$4('navigator', 'userAgent') || '';

    var global$m = global$q;
    var userAgent = engineUserAgent;

    var process = global$m.process;
    var Deno = global$m.Deno;
    var versions = process && process.versions || Deno && Deno.version;
    var v8 = versions && versions.v8;
    var match, version;

    if (v8) {
        match = v8.split('.');
        // in old Chrome, versions of V8 isn't V8 = Chrome / 10
        // but their correct versions are not interesting for us
        version = match[0] > 0 && match[0] < 4 ? 1 : +(match[0] + match[1]);
    }

    // BrowserFS NodeJS `process` polyfill incorrectly set `.v8` to `0.0`
    // so check `userAgent` even if `.v8` exists, but 0
    if (!version && userAgent) {
        match = userAgent.match(/Edge\/(\d+)/);
        if (!match || match[1] >= 74) {
            match = userAgent.match(/Chrome\/(\d+)/);
            if (match) version = +match[1];
        }
    }

    var engineV8Version = version;

    /* eslint-disable es-x/no-symbol -- required for testing */

    var V8_VERSION$2 = engineV8Version;
    var fails$8 = fails$c;

    // eslint-disable-next-line es-x/no-object-getownpropertysymbols -- required for testing
    var nativeSymbol = !!Object.getOwnPropertySymbols && !fails$8(function () {
        var symbol = Symbol();
        // Chrome 38 Symbol has incorrect toString conversion
        // `get-own-property-symbols` polyfill symbols converted to object are not Symbol instances
        return !String(symbol) || !(Object(symbol) instanceof Symbol) ||
            // Chrome 38-40 symbols are not inherited from DOM collections prototypes to instances
            !Symbol.sham && V8_VERSION$2 && V8_VERSION$2 < 41;
    });

    /* eslint-disable es-x/no-symbol -- required for testing */

    var NATIVE_SYMBOL$1 = nativeSymbol;

    var useSymbolAsUid = NATIVE_SYMBOL$1
        && !Symbol.sham
        && typeof Symbol.iterator == 'symbol';

    var global$l = global$q;
    var getBuiltIn$3 = getBuiltIn$5;
    var isCallable$9 = isCallable$c;
    var isPrototypeOf = objectIsPrototypeOf;
    var USE_SYMBOL_AS_UID$1 = useSymbolAsUid;

    var Object$3 = global$l.Object;

    var isSymbol$2 = USE_SYMBOL_AS_UID$1 ? function (it) {
        return typeof it == 'symbol';
    } : function (it) {
        var $Symbol = getBuiltIn$3('Symbol');
        return isCallable$9($Symbol) && isPrototypeOf($Symbol.prototype, Object$3(it));
    };

    var global$k = global$q;

    var String$3 = global$k.String;

    var tryToString$1 = function (argument) {
        try {
            return String$3(argument);
        } catch (error) {
            return 'Object';
        }
    };

    var global$j = global$q;
    var isCallable$8 = isCallable$c;
    var tryToString = tryToString$1;

    var TypeError$7 = global$j.TypeError;

    // `Assert: IsCallable(argument) is true`
    var aCallable$2 = function (argument) {
        if (isCallable$8(argument)) return argument;
        throw TypeError$7(tryToString(argument) + ' is not a function');
    };

    var aCallable$1 = aCallable$2;

    // `GetMethod` abstract operation
    // https://tc39.es/ecma262/#sec-getmethod
    var getMethod$1 = function (V, P) {
        var func = V[P];
        return func == null ? undefined : aCallable$1(func);
    };

    var global$i = global$q;
    var call$2 = functionCall;
    var isCallable$7 = isCallable$c;
    var isObject$6 = isObject$7;

    var TypeError$6 = global$i.TypeError;

    // `OrdinaryToPrimitive` abstract operation
    // https://tc39.es/ecma262/#sec-ordinarytoprimitive
    var ordinaryToPrimitive$1 = function (input, pref) {
        var fn, val;
        if (pref === 'string' && isCallable$7(fn = input.toString) && !isObject$6(val = call$2(fn, input))) return val;
        if (isCallable$7(fn = input.valueOf) && !isObject$6(val = call$2(fn, input))) return val;
        if (pref !== 'string' && isCallable$7(fn = input.toString) && !isObject$6(val = call$2(fn, input))) return val;
        throw TypeError$6("Can't convert object to primitive value");
    };

    var shared$3 = {exports: {}};

    var global$h = global$q;

    // eslint-disable-next-line es-x/no-object-defineproperty -- safe
    var defineProperty$1 = Object.defineProperty;

    var setGlobal$3 = function (key, value) {
        try {
            defineProperty$1(global$h, key, {value: value, configurable: true, writable: true});
        } catch (error) {
            global$h[key] = value;
        }
        return value;
    };

    var global$g = global$q;
    var setGlobal$2 = setGlobal$3;

    var SHARED = '__core-js_shared__';
    var store$3 = global$g[SHARED] || setGlobal$2(SHARED, {});

    var sharedStore = store$3;

    var store$2 = sharedStore;

    (shared$3.exports = function (key, value) {
        return store$2[key] || (store$2[key] = value !== undefined ? value : {});
    })('versions', []).push({
        version: '3.22.5',
        mode: 'global',
        copyright: '© 2014-2022 Denis Pushkarev (zloirock.ru)',
        license: 'https://github.com/zloirock/core-js/blob/v3.22.5/LICENSE',
        source: 'https://github.com/zloirock/core-js'
    });

    var global$f = global$q;
    var requireObjectCoercible$1 = requireObjectCoercible$3;

    var Object$2 = global$f.Object;

    // `ToObject` abstract operation
    // https://tc39.es/ecma262/#sec-toobject
    var toObject$3 = function (argument) {
        return Object$2(requireObjectCoercible$1(argument));
    };

    var uncurryThis$b = functionUncurryThis;
    var toObject$2 = toObject$3;

    var hasOwnProperty = uncurryThis$b({}.hasOwnProperty);

    // `HasOwnProperty` abstract operation
    // https://tc39.es/ecma262/#sec-hasownproperty
    // eslint-disable-next-line es-x/no-object-hasown -- safe
    var hasOwnProperty_1 = Object.hasOwn || function hasOwn(it, key) {
        return hasOwnProperty(toObject$2(it), key);
    };

    var uncurryThis$a = functionUncurryThis;

    var id = 0;
    var postfix = Math.random();
    var toString$4 = uncurryThis$a(1.0.toString);

    var uid$2 = function (key) {
        return 'Symbol(' + (key === undefined ? '' : key) + ')_' + toString$4(++id + postfix, 36);
    };

    var global$e = global$q;
    var shared$2 = shared$3.exports;
    var hasOwn$6 = hasOwnProperty_1;
    var uid$1 = uid$2;
    var NATIVE_SYMBOL = nativeSymbol;
    var USE_SYMBOL_AS_UID = useSymbolAsUid;

    var WellKnownSymbolsStore = shared$2('wks');
    var Symbol$2 = global$e.Symbol;
    var symbolFor = Symbol$2 && Symbol$2['for'];
    var createWellKnownSymbol = USE_SYMBOL_AS_UID ? Symbol$2 : Symbol$2 && Symbol$2.withoutSetter || uid$1;

    var wellKnownSymbol$7 = function (name) {
        if (!hasOwn$6(WellKnownSymbolsStore, name) || !(NATIVE_SYMBOL || typeof WellKnownSymbolsStore[name] == 'string')) {
            var description = 'Symbol.' + name;
            if (NATIVE_SYMBOL && hasOwn$6(Symbol$2, name)) {
                WellKnownSymbolsStore[name] = Symbol$2[name];
            } else if (USE_SYMBOL_AS_UID && symbolFor) {
                WellKnownSymbolsStore[name] = symbolFor(description);
            } else {
                WellKnownSymbolsStore[name] = createWellKnownSymbol(description);
            }
        }
        return WellKnownSymbolsStore[name];
    };

    var global$d = global$q;
    var call$1 = functionCall;
    var isObject$5 = isObject$7;
    var isSymbol$1 = isSymbol$2;
    var getMethod = getMethod$1;
    var ordinaryToPrimitive = ordinaryToPrimitive$1;
    var wellKnownSymbol$6 = wellKnownSymbol$7;

    var TypeError$5 = global$d.TypeError;
    var TO_PRIMITIVE = wellKnownSymbol$6('toPrimitive');

    // `ToPrimitive` abstract operation
    // https://tc39.es/ecma262/#sec-toprimitive
    var toPrimitive$1 = function (input, pref) {
        if (!isObject$5(input) || isSymbol$1(input)) return input;
        var exoticToPrim = getMethod(input, TO_PRIMITIVE);
        var result;
        if (exoticToPrim) {
            if (pref === undefined) pref = 'default';
            result = call$1(exoticToPrim, input, pref);
            if (!isObject$5(result) || isSymbol$1(result)) return result;
            throw TypeError$5("Can't convert object to primitive value");
        }
        if (pref === undefined) pref = 'number';
        return ordinaryToPrimitive(input, pref);
    };

    var toPrimitive = toPrimitive$1;
    var isSymbol = isSymbol$2;

    // `ToPropertyKey` abstract operation
    // https://tc39.es/ecma262/#sec-topropertykey
    var toPropertyKey$3 = function (argument) {
        var key = toPrimitive(argument, 'string');
        return isSymbol(key) ? key : key + '';
    };

    var global$c = global$q;
    var isObject$4 = isObject$7;

    var document$1 = global$c.document;
    // typeof document.createElement is 'object' in old IE
    var EXISTS$1 = isObject$4(document$1) && isObject$4(document$1.createElement);

    var documentCreateElement$1 = function (it) {
        return EXISTS$1 ? document$1.createElement(it) : {};
    };

    var DESCRIPTORS$8 = descriptors;
    var fails$7 = fails$c;
    var createElement = documentCreateElement$1;

    // Thanks to IE8 for its funny defineProperty
    var ie8DomDefine = !DESCRIPTORS$8 && !fails$7(function () {
        // eslint-disable-next-line es-x/no-object-defineproperty -- required for testing
        return Object.defineProperty(createElement('div'), 'a', {
            get: function () {
                return 7;
            }
        }).a != 7;
    });

    var DESCRIPTORS$7 = descriptors;
    var call = functionCall;
    var propertyIsEnumerableModule = objectPropertyIsEnumerable;
    var createPropertyDescriptor$2 = createPropertyDescriptor$3;
    var toIndexedObject$4 = toIndexedObject$5;
    var toPropertyKey$2 = toPropertyKey$3;
    var hasOwn$5 = hasOwnProperty_1;
    var IE8_DOM_DEFINE$1 = ie8DomDefine;

    // eslint-disable-next-line es-x/no-object-getownpropertydescriptor -- safe
    var $getOwnPropertyDescriptor$1 = Object.getOwnPropertyDescriptor;

    // `Object.getOwnPropertyDescriptor` method
    // https://tc39.es/ecma262/#sec-object.getownpropertydescriptor
    objectGetOwnPropertyDescriptor.f = DESCRIPTORS$7 ? $getOwnPropertyDescriptor$1 : function getOwnPropertyDescriptor(O, P) {
        O = toIndexedObject$4(O);
        P = toPropertyKey$2(P);
        if (IE8_DOM_DEFINE$1) try {
            return $getOwnPropertyDescriptor$1(O, P);
        } catch (error) { /* empty */
        }
        if (hasOwn$5(O, P)) return createPropertyDescriptor$2(!call(propertyIsEnumerableModule.f, O, P), O[P]);
    };

    var objectDefineProperty = {};

    var DESCRIPTORS$6 = descriptors;
    var fails$6 = fails$c;

    // V8 ~ Chrome 36-
    // https://bugs.chromium.org/p/v8/issues/detail?id=3334
    var v8PrototypeDefineBug = DESCRIPTORS$6 && fails$6(function () {
        // eslint-disable-next-line es-x/no-object-defineproperty -- required for testing
        return Object.defineProperty(function () { /* empty */
        }, 'prototype', {
            value: 42,
            writable: false
        }).prototype != 42;
    });

    var global$b = global$q;
    var isObject$3 = isObject$7;

    var String$2 = global$b.String;
    var TypeError$4 = global$b.TypeError;

    // `Assert: Type(argument) is Object`
    var anObject$4 = function (argument) {
        if (isObject$3(argument)) return argument;
        throw TypeError$4(String$2(argument) + ' is not an object');
    };

    var global$a = global$q;
    var DESCRIPTORS$5 = descriptors;
    var IE8_DOM_DEFINE = ie8DomDefine;
    var V8_PROTOTYPE_DEFINE_BUG$1 = v8PrototypeDefineBug;
    var anObject$3 = anObject$4;
    var toPropertyKey$1 = toPropertyKey$3;

    var TypeError$3 = global$a.TypeError;
    // eslint-disable-next-line es-x/no-object-defineproperty -- safe
    var $defineProperty = Object.defineProperty;
    // eslint-disable-next-line es-x/no-object-getownpropertydescriptor -- safe
    var $getOwnPropertyDescriptor = Object.getOwnPropertyDescriptor;
    var ENUMERABLE = 'enumerable';
    var CONFIGURABLE$1 = 'configurable';
    var WRITABLE = 'writable';

    // `Object.defineProperty` method
    // https://tc39.es/ecma262/#sec-object.defineproperty
    objectDefineProperty.f = DESCRIPTORS$5 ? V8_PROTOTYPE_DEFINE_BUG$1 ? function defineProperty(O, P, Attributes) {
        anObject$3(O);
        P = toPropertyKey$1(P);
        anObject$3(Attributes);
        if (typeof O === 'function' && P === 'prototype' && 'value' in Attributes && WRITABLE in Attributes && !Attributes[WRITABLE]) {
            var current = $getOwnPropertyDescriptor(O, P);
            if (current && current[WRITABLE]) {
                O[P] = Attributes.value;
                Attributes = {
                    configurable: CONFIGURABLE$1 in Attributes ? Attributes[CONFIGURABLE$1] : current[CONFIGURABLE$1],
                    enumerable: ENUMERABLE in Attributes ? Attributes[ENUMERABLE] : current[ENUMERABLE],
                    writable: false
                };
            }
        }
        return $defineProperty(O, P, Attributes);
    } : $defineProperty : function defineProperty(O, P, Attributes) {
        anObject$3(O);
        P = toPropertyKey$1(P);
        anObject$3(Attributes);
        if (IE8_DOM_DEFINE) try {
            return $defineProperty(O, P, Attributes);
        } catch (error) { /* empty */
        }
        if ('get' in Attributes || 'set' in Attributes) throw TypeError$3('Accessors not supported');
        if ('value' in Attributes) O[P] = Attributes.value;
        return O;
    };

    var DESCRIPTORS$4 = descriptors;
    var definePropertyModule$4 = objectDefineProperty;
    var createPropertyDescriptor$1 = createPropertyDescriptor$3;

    var createNonEnumerableProperty$3 = DESCRIPTORS$4 ? function (object, key, value) {
        return definePropertyModule$4.f(object, key, createPropertyDescriptor$1(1, value));
    } : function (object, key, value) {
        object[key] = value;
        return object;
    };

    var makeBuiltIn$2 = {exports: {}};

    var DESCRIPTORS$3 = descriptors;
    var hasOwn$4 = hasOwnProperty_1;

    var FunctionPrototype = Function.prototype;
    // eslint-disable-next-line es-x/no-object-getownpropertydescriptor -- safe
    var getDescriptor = DESCRIPTORS$3 && Object.getOwnPropertyDescriptor;

    var EXISTS = hasOwn$4(FunctionPrototype, 'name');
    // additional protection from minified / mangled / dropped function names
    var PROPER = EXISTS && (function something() { /* empty */
    }).name === 'something';
    var CONFIGURABLE = EXISTS && (!DESCRIPTORS$3 || (DESCRIPTORS$3 && getDescriptor(FunctionPrototype, 'name').configurable));

    var functionName = {
        EXISTS: EXISTS,
        PROPER: PROPER,
        CONFIGURABLE: CONFIGURABLE
    };

    var uncurryThis$9 = functionUncurryThis;
    var isCallable$6 = isCallable$c;
    var store$1 = sharedStore;

    var functionToString = uncurryThis$9(Function.toString);

    // this helper broken in `core-js@3.4.1-3.4.4`, so we can't use `shared` helper
    if (!isCallable$6(store$1.inspectSource)) {
        store$1.inspectSource = function (it) {
            return functionToString(it);
        };
    }

    var inspectSource$3 = store$1.inspectSource;

    var global$9 = global$q;
    var isCallable$5 = isCallable$c;
    var inspectSource$2 = inspectSource$3;

    var WeakMap$1 = global$9.WeakMap;

    var nativeWeakMap = isCallable$5(WeakMap$1) && /native code/.test(inspectSource$2(WeakMap$1));

    var shared$1 = shared$3.exports;
    var uid = uid$2;

    var keys = shared$1('keys');

    var sharedKey$2 = function (key) {
        return keys[key] || (keys[key] = uid(key));
    };

    var hiddenKeys$4 = {};

    var NATIVE_WEAK_MAP = nativeWeakMap;
    var global$8 = global$q;
    var uncurryThis$8 = functionUncurryThis;
    var isObject$2 = isObject$7;
    var createNonEnumerableProperty$2 = createNonEnumerableProperty$3;
    var hasOwn$3 = hasOwnProperty_1;
    var shared = sharedStore;
    var sharedKey$1 = sharedKey$2;
    var hiddenKeys$3 = hiddenKeys$4;

    var OBJECT_ALREADY_INITIALIZED = 'Object already initialized';
    var TypeError$2 = global$8.TypeError;
    var WeakMap = global$8.WeakMap;
    var set, get, has;

    var enforce = function (it) {
        return has(it) ? get(it) : set(it, {});
    };

    var getterFor = function (TYPE) {
        return function (it) {
            var state;
            if (!isObject$2(it) || (state = get(it)).type !== TYPE) {
                throw TypeError$2('Incompatible receiver, ' + TYPE + ' required');
            }
            return state;
        };
    };

    if (NATIVE_WEAK_MAP || shared.state) {
        var store = shared.state || (shared.state = new WeakMap());
        var wmget = uncurryThis$8(store.get);
        var wmhas = uncurryThis$8(store.has);
        var wmset = uncurryThis$8(store.set);
        set = function (it, metadata) {
            if (wmhas(store, it)) throw new TypeError$2(OBJECT_ALREADY_INITIALIZED);
            metadata.facade = it;
            wmset(store, it, metadata);
            return metadata;
        };
        get = function (it) {
            return wmget(store, it) || {};
        };
        has = function (it) {
            return wmhas(store, it);
        };
    } else {
        var STATE = sharedKey$1('state');
        hiddenKeys$3[STATE] = true;
        set = function (it, metadata) {
            if (hasOwn$3(it, STATE)) throw new TypeError$2(OBJECT_ALREADY_INITIALIZED);
            metadata.facade = it;
            createNonEnumerableProperty$2(it, STATE, metadata);
            return metadata;
        };
        get = function (it) {
            return hasOwn$3(it, STATE) ? it[STATE] : {};
        };
        has = function (it) {
            return hasOwn$3(it, STATE);
        };
    }

    var internalState = {
        set: set,
        get: get,
        has: has,
        enforce: enforce,
        getterFor: getterFor
    };

    var fails$5 = fails$c;
    var isCallable$4 = isCallable$c;
    var hasOwn$2 = hasOwnProperty_1;
    var DESCRIPTORS$2 = descriptors;
    var CONFIGURABLE_FUNCTION_NAME = functionName.CONFIGURABLE;
    var inspectSource$1 = inspectSource$3;
    var InternalStateModule = internalState;

    var enforceInternalState = InternalStateModule.enforce;
    var getInternalState = InternalStateModule.get;
    // eslint-disable-next-line es-x/no-object-defineproperty -- safe
    var defineProperty = Object.defineProperty;

    var CONFIGURABLE_LENGTH = DESCRIPTORS$2 && !fails$5(function () {
        return defineProperty(function () { /* empty */
        }, 'length', {value: 8}).length !== 8;
    });

    var TEMPLATE = String(String).split('String');

    var makeBuiltIn$1 = makeBuiltIn$2.exports = function (value, name, options) {
        if (String(name).slice(0, 7) === 'Symbol(') {
            name = '[' + String(name).replace(/^Symbol\(([^)]*)\)/, '$1') + ']';
        }
        if (options && options.getter) name = 'get ' + name;
        if (options && options.setter) name = 'set ' + name;
        if (!hasOwn$2(value, 'name') || (CONFIGURABLE_FUNCTION_NAME && value.name !== name)) {
            defineProperty(value, 'name', {value: name, configurable: true});
        }
        if (CONFIGURABLE_LENGTH && options && hasOwn$2(options, 'arity') && value.length !== options.arity) {
            defineProperty(value, 'length', {value: options.arity});
        }
        if (options && hasOwn$2(options, 'constructor') && options.constructor) {
            if (DESCRIPTORS$2) try {
                defineProperty(value, 'prototype', {writable: false});
            } catch (error) { /* empty */
            }
        } else value.prototype = undefined;
        var state = enforceInternalState(value);
        if (!hasOwn$2(state, 'source')) {
            state.source = TEMPLATE.join(typeof name == 'string' ? name : '');
        }
        return value;
    };

    // add fake Function#toString for correct work wrapped methods / constructors with methods like LoDash isNative
    // eslint-disable-next-line no-extend-native -- required
    Function.prototype.toString = makeBuiltIn$1(function toString() {
        return isCallable$4(this) && getInternalState(this).source || inspectSource$1(this);
    }, 'toString');

    var global$7 = global$q;
    var isCallable$3 = isCallable$c;
    var createNonEnumerableProperty$1 = createNonEnumerableProperty$3;
    var makeBuiltIn = makeBuiltIn$2.exports;
    var setGlobal$1 = setGlobal$3;

    var defineBuiltIn$2 = function (O, key, value, options) {
        var unsafe = options ? !!options.unsafe : false;
        var simple = options ? !!options.enumerable : false;
        var noTargetGet = options ? !!options.noTargetGet : false;
        var name = options && options.name !== undefined ? options.name : key;
        if (isCallable$3(value)) makeBuiltIn(value, name, options);
        if (O === global$7) {
            if (simple) O[key] = value;
            else setGlobal$1(key, value);
            return O;
        } else if (!unsafe) {
            delete O[key];
        } else if (!noTargetGet && O[key]) {
            simple = true;
        }
        if (simple) O[key] = value;
        else createNonEnumerableProperty$1(O, key, value);
        return O;
    };

    var objectGetOwnPropertyNames = {};

    var ceil = Math.ceil;
    var floor = Math.floor;

    // `ToIntegerOrInfinity` abstract operation
    // https://tc39.es/ecma262/#sec-tointegerorinfinity
    var toIntegerOrInfinity$2 = function (argument) {
        var number = +argument;
        // eslint-disable-next-line no-self-compare -- safe
        return number !== number || number === 0 ? 0 : (number > 0 ? floor : ceil)(number);
    };

    var toIntegerOrInfinity$1 = toIntegerOrInfinity$2;

    var max = Math.max;
    var min$1 = Math.min;

    // Helper for a popular repeating case of the spec:
    // Let integer be ? ToInteger(index).
    // If integer < 0, let result be max((length + integer), 0); else let result be min(integer, length).
    var toAbsoluteIndex$1 = function (index, length) {
        var integer = toIntegerOrInfinity$1(index);
        return integer < 0 ? max(integer + length, 0) : min$1(integer, length);
    };

    var toIntegerOrInfinity = toIntegerOrInfinity$2;

    var min = Math.min;

    // `ToLength` abstract operation
    // https://tc39.es/ecma262/#sec-tolength
    var toLength$1 = function (argument) {
        return argument > 0 ? min(toIntegerOrInfinity(argument), 0x1FFFFFFFFFFFFF) : 0; // 2 ** 53 - 1 == 9007199254740991
    };

    var toLength = toLength$1;

    // `LengthOfArrayLike` abstract operation
    // https://tc39.es/ecma262/#sec-lengthofarraylike
    var lengthOfArrayLike$3 = function (obj) {
        return toLength(obj.length);
    };

    var toIndexedObject$3 = toIndexedObject$5;
    var toAbsoluteIndex = toAbsoluteIndex$1;
    var lengthOfArrayLike$2 = lengthOfArrayLike$3;

    // `Array.prototype.{ indexOf, includes }` methods implementation
    var createMethod$3 = function (IS_INCLUDES) {
        return function ($this, el, fromIndex) {
            var O = toIndexedObject$3($this);
            var length = lengthOfArrayLike$2(O);
            var index = toAbsoluteIndex(fromIndex, length);
            var value;
            // Array#includes uses SameValueZero equality algorithm
            // eslint-disable-next-line no-self-compare -- NaN check
            if (IS_INCLUDES && el != el) while (length > index) {
                value = O[index++];
                // eslint-disable-next-line no-self-compare -- NaN check
                if (value != value) return true;
                // Array#indexOf ignores holes, Array#includes - not
            } else for (; length > index; index++) {
                if ((IS_INCLUDES || index in O) && O[index] === el) return IS_INCLUDES || index || 0;
            }
            return !IS_INCLUDES && -1;
        };
    };

    var arrayIncludes = {
        // `Array.prototype.includes` method
        // https://tc39.es/ecma262/#sec-array.prototype.includes
        includes: createMethod$3(true),
        // `Array.prototype.indexOf` method
        // https://tc39.es/ecma262/#sec-array.prototype.indexof
        indexOf: createMethod$3(false)
    };

    var uncurryThis$7 = functionUncurryThis;
    var hasOwn$1 = hasOwnProperty_1;
    var toIndexedObject$2 = toIndexedObject$5;
    var indexOf = arrayIncludes.indexOf;
    var hiddenKeys$2 = hiddenKeys$4;

    var push$2 = uncurryThis$7([].push);

    var objectKeysInternal = function (object, names) {
        var O = toIndexedObject$2(object);
        var i = 0;
        var result = [];
        var key;
        for (key in O) !hasOwn$1(hiddenKeys$2, key) && hasOwn$1(O, key) && push$2(result, key);
        // Don't enum bug & hidden keys
        while (names.length > i) if (hasOwn$1(O, key = names[i++])) {
            ~indexOf(result, key) || push$2(result, key);
        }
        return result;
    };

    // IE8- don't enum bug keys
    var enumBugKeys$3 = [
        'constructor',
        'hasOwnProperty',
        'isPrototypeOf',
        'propertyIsEnumerable',
        'toLocaleString',
        'toString',
        'valueOf'
    ];

    var internalObjectKeys$1 = objectKeysInternal;
    var enumBugKeys$2 = enumBugKeys$3;

    var hiddenKeys$1 = enumBugKeys$2.concat('length', 'prototype');

    // `Object.getOwnPropertyNames` method
    // https://tc39.es/ecma262/#sec-object.getownpropertynames
    // eslint-disable-next-line es-x/no-object-getownpropertynames -- safe
    objectGetOwnPropertyNames.f = Object.getOwnPropertyNames || function getOwnPropertyNames(O) {
        return internalObjectKeys$1(O, hiddenKeys$1);
    };

    var objectGetOwnPropertySymbols = {};

    // eslint-disable-next-line es-x/no-object-getownpropertysymbols -- safe
    objectGetOwnPropertySymbols.f = Object.getOwnPropertySymbols;

    var getBuiltIn$2 = getBuiltIn$5;
    var uncurryThis$6 = functionUncurryThis;
    var getOwnPropertyNamesModule = objectGetOwnPropertyNames;
    var getOwnPropertySymbolsModule = objectGetOwnPropertySymbols;
    var anObject$2 = anObject$4;

    var concat = uncurryThis$6([].concat);

    // all object keys, includes non-enumerable and symbols
    var ownKeys$1 = getBuiltIn$2('Reflect', 'ownKeys') || function ownKeys(it) {
        var keys = getOwnPropertyNamesModule.f(anObject$2(it));
        var getOwnPropertySymbols = getOwnPropertySymbolsModule.f;
        return getOwnPropertySymbols ? concat(keys, getOwnPropertySymbols(it)) : keys;
    };

    var hasOwn = hasOwnProperty_1;
    var ownKeys = ownKeys$1;
    var getOwnPropertyDescriptorModule = objectGetOwnPropertyDescriptor;
    var definePropertyModule$3 = objectDefineProperty;

    var copyConstructorProperties$1 = function (target, source, exceptions) {
        var keys = ownKeys(source);
        var defineProperty = definePropertyModule$3.f;
        var getOwnPropertyDescriptor = getOwnPropertyDescriptorModule.f;
        for (var i = 0; i < keys.length; i++) {
            var key = keys[i];
            if (!hasOwn(target, key) && !(exceptions && hasOwn(exceptions, key))) {
                defineProperty(target, key, getOwnPropertyDescriptor(source, key));
            }
        }
    };

    var fails$4 = fails$c;
    var isCallable$2 = isCallable$c;

    var replacement = /#|\.prototype\./;

    var isForced$1 = function (feature, detection) {
        var value = data[normalize(feature)];
        return value == POLYFILL ? true
            : value == NATIVE ? false
                : isCallable$2(detection) ? fails$4(detection)
                    : !!detection;
    };

    var normalize = isForced$1.normalize = function (string) {
        return String(string).replace(replacement, '.').toLowerCase();
    };

    var data = isForced$1.data = {};
    var NATIVE = isForced$1.NATIVE = 'N';
    var POLYFILL = isForced$1.POLYFILL = 'P';

    var isForced_1 = isForced$1;

    var global$6 = global$q;
    var getOwnPropertyDescriptor = objectGetOwnPropertyDescriptor.f;
    var createNonEnumerableProperty = createNonEnumerableProperty$3;
    var defineBuiltIn$1 = defineBuiltIn$2;
    var setGlobal = setGlobal$3;
    var copyConstructorProperties = copyConstructorProperties$1;
    var isForced = isForced_1;

    /*
      options.target      - name of the target object
      options.global      - target is the global object
      options.stat        - export as static methods of target
      options.proto       - export as prototype methods of target
      options.real        - real prototype method for the `pure` version
      options.forced      - export even if the native feature is available
      options.bind        - bind methods to the target, required for the `pure` version
      options.wrap        - wrap constructors to preventing global pollution, required for the `pure` version
      options.unsafe      - use the simple assignment of property instead of delete + defineProperty
      options.sham        - add a flag to not completely full polyfills
      options.enumerable  - export as enumerable property
      options.noTargetGet - prevent calling a getter on target
      options.name        - the .name of the function if it does not match the key
    */
    var _export = function (options, source) {
        var TARGET = options.target;
        var GLOBAL = options.global;
        var STATIC = options.stat;
        var FORCED, target, key, targetProperty, sourceProperty, descriptor;
        if (GLOBAL) {
            target = global$6;
        } else if (STATIC) {
            target = global$6[TARGET] || setGlobal(TARGET, {});
        } else {
            target = (global$6[TARGET] || {}).prototype;
        }
        if (target) for (key in source) {
            sourceProperty = source[key];
            if (options.noTargetGet) {
                descriptor = getOwnPropertyDescriptor(target, key);
                targetProperty = descriptor && descriptor.value;
            } else targetProperty = target[key];
            FORCED = isForced(GLOBAL ? key : TARGET + (STATIC ? '.' : '#') + key, options.forced);
            // contained in target
            if (!FORCED && targetProperty !== undefined) {
                if (typeof sourceProperty == typeof targetProperty) continue;
                copyConstructorProperties(sourceProperty, targetProperty);
            }
            // add a flag to not completely full polyfills
            if (options.sham || (targetProperty && targetProperty.sham)) {
                createNonEnumerableProperty(sourceProperty, 'sham', true);
            }
            defineBuiltIn$1(target, key, sourceProperty, options);
        }
    };

    var internalObjectKeys = objectKeysInternal;
    var enumBugKeys$1 = enumBugKeys$3;

    // `Object.keys` method
    // https://tc39.es/ecma262/#sec-object.keys
    // eslint-disable-next-line es-x/no-object-keys -- safe
    var objectKeys$2 = Object.keys || function keys(O) {
        return internalObjectKeys(O, enumBugKeys$1);
    };

    var DESCRIPTORS$1 = descriptors;
    var uncurryThis$5 = functionUncurryThis;
    var objectKeys$1 = objectKeys$2;
    var toIndexedObject$1 = toIndexedObject$5;
    var $propertyIsEnumerable = objectPropertyIsEnumerable.f;

    var propertyIsEnumerable = uncurryThis$5($propertyIsEnumerable);
    var push$1 = uncurryThis$5([].push);

    // `Object.{ entries, values }` methods implementation
    var createMethod$2 = function (TO_ENTRIES) {
        return function (it) {
            var O = toIndexedObject$1(it);
            var keys = objectKeys$1(O);
            var length = keys.length;
            var i = 0;
            var result = [];
            var key;
            while (length > i) {
                key = keys[i++];
                if (!DESCRIPTORS$1 || propertyIsEnumerable(O, key)) {
                    push$1(result, TO_ENTRIES ? [key, O[key]] : O[key]);
                }
            }
            return result;
        };
    };

    var objectToArray = {
        // `Object.entries` method
        // https://tc39.es/ecma262/#sec-object.entries
        entries: createMethod$2(true),
        // `Object.values` method
        // https://tc39.es/ecma262/#sec-object.values
        values: createMethod$2(false)
    };

    var $$4 = _export;
    var $entries = objectToArray.entries;

    // `Object.entries` method
    // https://tc39.es/ecma262/#sec-object.entries
    $$4({target: 'Object', stat: true}, {
        entries: function entries(O) {
            return $entries(O);
        }
    });

    var uncurryThis$4 = functionUncurryThis;
    var aCallable = aCallable$2;
    var NATIVE_BIND = functionBindNative;

    var bind$1 = uncurryThis$4(uncurryThis$4.bind);

    // optional / simple context binding
    var functionBindContext = function (fn, that) {
        aCallable(fn);
        return that === undefined ? fn : NATIVE_BIND ? bind$1(fn, that) : function (/* ...args */) {
            return fn.apply(that, arguments);
        };
    };

    var classof$4 = classofRaw$1;

    // `IsArray` abstract operation
    // https://tc39.es/ecma262/#sec-isarray
    // eslint-disable-next-line es-x/no-array-isarray -- safe
    var isArray$2 = Array.isArray || function isArray(argument) {
        return classof$4(argument) == 'Array';
    };

    var wellKnownSymbol$5 = wellKnownSymbol$7;

    var TO_STRING_TAG$1 = wellKnownSymbol$5('toStringTag');
    var test = {};

    test[TO_STRING_TAG$1] = 'z';

    var toStringTagSupport = String(test) === '[object z]';

    var global$5 = global$q;
    var TO_STRING_TAG_SUPPORT$2 = toStringTagSupport;
    var isCallable$1 = isCallable$c;
    var classofRaw = classofRaw$1;
    var wellKnownSymbol$4 = wellKnownSymbol$7;

    var TO_STRING_TAG = wellKnownSymbol$4('toStringTag');
    var Object$1 = global$5.Object;

    // ES3 wrong here
    var CORRECT_ARGUMENTS = classofRaw(function () {
        return arguments;
    }()) == 'Arguments';

    // fallback for IE11 Script Access Denied error
    var tryGet = function (it, key) {
        try {
            return it[key];
        } catch (error) { /* empty */
        }
    };

    // getting tag from ES6+ `Object.prototype.toString`
    var classof$3 = TO_STRING_TAG_SUPPORT$2 ? classofRaw : function (it) {
        var O, tag, result;
        return it === undefined ? 'Undefined' : it === null ? 'Null'
            // @@toStringTag case
            : typeof (tag = tryGet(O = Object$1(it), TO_STRING_TAG)) == 'string' ? tag
                // builtinTag case
                : CORRECT_ARGUMENTS ? classofRaw(O)
                    // ES3 arguments fallback
                    : (result = classofRaw(O)) == 'Object' && isCallable$1(O.callee) ? 'Arguments' : result;
    };

    var uncurryThis$3 = functionUncurryThis;
    var fails$3 = fails$c;
    var isCallable = isCallable$c;
    var classof$2 = classof$3;
    var getBuiltIn$1 = getBuiltIn$5;
    var inspectSource = inspectSource$3;

    var noop = function () { /* empty */
    };
    var empty = [];
    var construct = getBuiltIn$1('Reflect', 'construct');
    var constructorRegExp = /^\s*(?:class|function)\b/;
    var exec$1 = uncurryThis$3(constructorRegExp.exec);
    var INCORRECT_TO_STRING = !constructorRegExp.exec(noop);

    var isConstructorModern = function isConstructor(argument) {
        if (!isCallable(argument)) return false;
        try {
            construct(noop, empty, argument);
            return true;
        } catch (error) {
            return false;
        }
    };

    var isConstructorLegacy = function isConstructor(argument) {
        if (!isCallable(argument)) return false;
        switch (classof$2(argument)) {
            case 'AsyncFunction':
            case 'GeneratorFunction':
            case 'AsyncGeneratorFunction':
                return false;
        }
        try {
            // we can't check .prototype since constructors produced by .bind haven't it
            // `Function#toString` throws on some built-it function in some legacy engines
            // (for example, `DOMQuad` and similar in FF41-)
            return INCORRECT_TO_STRING || !!exec$1(constructorRegExp, inspectSource(argument));
        } catch (error) {
            return true;
        }
    };

    isConstructorLegacy.sham = true;

    // `IsConstructor` abstract operation
    // https://tc39.es/ecma262/#sec-isconstructor
    var isConstructor$1 = !construct || fails$3(function () {
        var called;
        return isConstructorModern(isConstructorModern.call)
            || !isConstructorModern(Object)
            || !isConstructorModern(function () {
                called = true;
            })
            || called;
    }) ? isConstructorLegacy : isConstructorModern;

    var global$4 = global$q;
    var isArray$1 = isArray$2;
    var isConstructor = isConstructor$1;
    var isObject$1 = isObject$7;
    var wellKnownSymbol$3 = wellKnownSymbol$7;

    var SPECIES$1 = wellKnownSymbol$3('species');
    var Array$1 = global$4.Array;

    // a part of `ArraySpeciesCreate` abstract operation
    // https://tc39.es/ecma262/#sec-arrayspeciescreate
    var arraySpeciesConstructor$1 = function (originalArray) {
        var C;
        if (isArray$1(originalArray)) {
            C = originalArray.constructor;
            // cross-realm fallback
            if (isConstructor(C) && (C === Array$1 || isArray$1(C.prototype))) C = undefined;
            else if (isObject$1(C)) {
                C = C[SPECIES$1];
                if (C === null) C = undefined;
            }
        }
        return C === undefined ? Array$1 : C;
    };

    var arraySpeciesConstructor = arraySpeciesConstructor$1;

    // `ArraySpeciesCreate` abstract operation
    // https://tc39.es/ecma262/#sec-arrayspeciescreate
    var arraySpeciesCreate$2 = function (originalArray, length) {
        return new (arraySpeciesConstructor(originalArray))(length === 0 ? 0 : length);
    };

    var bind = functionBindContext;
    var uncurryThis$2 = functionUncurryThis;
    var IndexedObject = indexedObject;
    var toObject$1 = toObject$3;
    var lengthOfArrayLike$1 = lengthOfArrayLike$3;
    var arraySpeciesCreate$1 = arraySpeciesCreate$2;

    var push = uncurryThis$2([].push);

    // `Array.prototype.{ forEach, map, filter, some, every, find, findIndex, filterReject }` methods implementation
    var createMethod$1 = function (TYPE) {
        var IS_MAP = TYPE == 1;
        var IS_FILTER = TYPE == 2;
        var IS_SOME = TYPE == 3;
        var IS_EVERY = TYPE == 4;
        var IS_FIND_INDEX = TYPE == 6;
        var IS_FILTER_REJECT = TYPE == 7;
        var NO_HOLES = TYPE == 5 || IS_FIND_INDEX;
        return function ($this, callbackfn, that, specificCreate) {
            var O = toObject$1($this);
            var self = IndexedObject(O);
            var boundFunction = bind(callbackfn, that);
            var length = lengthOfArrayLike$1(self);
            var index = 0;
            var create = specificCreate || arraySpeciesCreate$1;
            var target = IS_MAP ? create($this, length) : IS_FILTER || IS_FILTER_REJECT ? create($this, 0) : undefined;
            var value, result;
            for (; length > index; index++) if (NO_HOLES || index in self) {
                value = self[index];
                result = boundFunction(value, index, O);
                if (TYPE) {
                    if (IS_MAP) target[index] = result; // map
                    else if (result) switch (TYPE) {
                        case 3:
                            return true;              // some
                        case 5:
                            return value;             // find
                        case 6:
                            return index;             // findIndex
                        case 2:
                            push(target, value);      // filter
                    } else switch (TYPE) {
                        case 4:
                            return false;             // every
                        case 7:
                            push(target, value);      // filterReject
                    }
                }
            }
            return IS_FIND_INDEX ? -1 : IS_SOME || IS_EVERY ? IS_EVERY : target;
        };
    };

    var arrayIteration = {
        // `Array.prototype.forEach` method
        // https://tc39.es/ecma262/#sec-array.prototype.foreach
        forEach: createMethod$1(0),
        // `Array.prototype.map` method
        // https://tc39.es/ecma262/#sec-array.prototype.map
        map: createMethod$1(1),
        // `Array.prototype.filter` method
        // https://tc39.es/ecma262/#sec-array.prototype.filter
        filter: createMethod$1(2),
        // `Array.prototype.some` method
        // https://tc39.es/ecma262/#sec-array.prototype.some
        some: createMethod$1(3),
        // `Array.prototype.every` method
        // https://tc39.es/ecma262/#sec-array.prototype.every
        every: createMethod$1(4),
        // `Array.prototype.find` method
        // https://tc39.es/ecma262/#sec-array.prototype.find
        find: createMethod$1(5),
        // `Array.prototype.findIndex` method
        // https://tc39.es/ecma262/#sec-array.prototype.findIndex
        findIndex: createMethod$1(6),
        // `Array.prototype.filterReject` method
        // https://github.com/tc39/proposal-array-filtering
        filterReject: createMethod$1(7)
    };

    var objectDefineProperties = {};

    var DESCRIPTORS = descriptors;
    var V8_PROTOTYPE_DEFINE_BUG = v8PrototypeDefineBug;
    var definePropertyModule$2 = objectDefineProperty;
    var anObject$1 = anObject$4;
    var toIndexedObject = toIndexedObject$5;
    var objectKeys = objectKeys$2;

    // `Object.defineProperties` method
    // https://tc39.es/ecma262/#sec-object.defineproperties
    // eslint-disable-next-line es-x/no-object-defineproperties -- safe
    objectDefineProperties.f = DESCRIPTORS && !V8_PROTOTYPE_DEFINE_BUG ? Object.defineProperties : function defineProperties(O, Properties) {
        anObject$1(O);
        var props = toIndexedObject(Properties);
        var keys = objectKeys(Properties);
        var length = keys.length;
        var index = 0;
        var key;
        while (length > index) definePropertyModule$2.f(O, key = keys[index++], props[key]);
        return O;
    };

    var getBuiltIn = getBuiltIn$5;

    var html$1 = getBuiltIn('document', 'documentElement');

    /* global ActiveXObject -- old IE, WSH */

    var anObject = anObject$4;
    var definePropertiesModule = objectDefineProperties;
    var enumBugKeys = enumBugKeys$3;
    var hiddenKeys = hiddenKeys$4;
    var html = html$1;
    var documentCreateElement = documentCreateElement$1;
    var sharedKey = sharedKey$2;

    var GT = '>';
    var LT = '<';
    var PROTOTYPE = 'prototype';
    var SCRIPT = 'script';
    var IE_PROTO = sharedKey('IE_PROTO');

    var EmptyConstructor = function () { /* empty */
    };

    var scriptTag = function (content) {
        return LT + SCRIPT + GT + content + LT + '/' + SCRIPT + GT;
    };

    // Create object with fake `null` prototype: use ActiveX Object with cleared prototype
    var NullProtoObjectViaActiveX = function (activeXDocument) {
        activeXDocument.write(scriptTag(''));
        activeXDocument.close();
        var temp = activeXDocument.parentWindow.Object;
        activeXDocument = null; // avoid memory leak
        return temp;
    };

    // Create object with fake `null` prototype: use iframe Object with cleared prototype
    var NullProtoObjectViaIFrame = function () {
        // Thrash, waste and sodomy: IE GC bug
        var iframe = documentCreateElement('iframe');
        var JS = 'java' + SCRIPT + ':';
        var iframeDocument;
        iframe.style.display = 'none';
        html.appendChild(iframe);
        // https://github.com/zloirock/core-js/issues/475
        iframe.src = String(JS);
        iframeDocument = iframe.contentWindow.document;
        iframeDocument.open();
        iframeDocument.write(scriptTag('document.F=Object'));
        iframeDocument.close();
        return iframeDocument.F;
    };

    // Check for document.domain and active x support
    // No need to use active x approach when document.domain is not set
    // see https://github.com/es-shims/es5-shim/issues/150
    // variation of https://github.com/kitcambridge/es5-shim/commit/4f738ac066346
    // avoid IE GC bug
    var activeXDocument;
    var NullProtoObject = function () {
        try {
            activeXDocument = new ActiveXObject('htmlfile');
        } catch (error) { /* ignore */
        }
        NullProtoObject = typeof document != 'undefined'
            ? document.domain && activeXDocument
                ? NullProtoObjectViaActiveX(activeXDocument) // old IE
                : NullProtoObjectViaIFrame()
            : NullProtoObjectViaActiveX(activeXDocument); // WSH
        var length = enumBugKeys.length;
        while (length--) delete NullProtoObject[PROTOTYPE][enumBugKeys[length]];
        return NullProtoObject();
    };

    hiddenKeys[IE_PROTO] = true;

    // `Object.create` method
    // https://tc39.es/ecma262/#sec-object.create
    // eslint-disable-next-line es-x/no-object-create -- safe
    var objectCreate = Object.create || function create(O, Properties) {
        var result;
        if (O !== null) {
            EmptyConstructor[PROTOTYPE] = anObject(O);
            result = new EmptyConstructor();
            EmptyConstructor[PROTOTYPE] = null;
            // add "__proto__" for Object.getPrototypeOf polyfill
            result[IE_PROTO] = O;
        } else result = NullProtoObject();
        return Properties === undefined ? result : definePropertiesModule.f(result, Properties);
    };

    var wellKnownSymbol$2 = wellKnownSymbol$7;
    var create = objectCreate;
    var definePropertyModule$1 = objectDefineProperty;

    var UNSCOPABLES = wellKnownSymbol$2('unscopables');
    var ArrayPrototype = Array.prototype;

    // Array.prototype[@@unscopables]
    // https://tc39.es/ecma262/#sec-array.prototype-@@unscopables
    if (ArrayPrototype[UNSCOPABLES] == undefined) {
        definePropertyModule$1.f(ArrayPrototype, UNSCOPABLES, {
            configurable: true,
            value: create(null)
        });
    }

    // add a key to Array.prototype[@@unscopables]
    var addToUnscopables$1 = function (key) {
        ArrayPrototype[UNSCOPABLES][key] = true;
    };

    var $$3 = _export;
    var $find = arrayIteration.find;
    var addToUnscopables = addToUnscopables$1;

    var FIND = 'find';
    var SKIPS_HOLES = true;

    // Shouldn't skip holes
    if (FIND in []) Array(1)[FIND](function () {
        SKIPS_HOLES = false;
    });

    // `Array.prototype.find` method
    // https://tc39.es/ecma262/#sec-array.prototype.find
    $$3({target: 'Array', proto: true, forced: SKIPS_HOLES}, {
        find: function find(callbackfn /* , that = undefined */) {
            return $find(this, callbackfn, arguments.length > 1 ? arguments[1] : undefined);
        }
    });

    // https://tc39.es/ecma262/#sec-array.prototype-@@unscopables
    addToUnscopables(FIND);

    var TO_STRING_TAG_SUPPORT$1 = toStringTagSupport;
    var classof$1 = classof$3;

    // `Object.prototype.toString` method implementation
    // https://tc39.es/ecma262/#sec-object.prototype.tostring
    var objectToString = TO_STRING_TAG_SUPPORT$1 ? {}.toString : function toString() {
        return '[object ' + classof$1(this) + ']';
    };

    var TO_STRING_TAG_SUPPORT = toStringTagSupport;
    var defineBuiltIn = defineBuiltIn$2;
    var toString$3 = objectToString;

    // `Object.prototype.toString` method
    // https://tc39.es/ecma262/#sec-object.prototype.tostring
    if (!TO_STRING_TAG_SUPPORT) {
        defineBuiltIn(Object.prototype, 'toString', toString$3, {unsafe: true});
    }

    var global$3 = global$q;
    var classof = classof$3;

    var String$1 = global$3.String;

    var toString$2 = function (argument) {
        if (classof(argument) === 'Symbol') throw TypeError('Cannot convert a Symbol value to a string');
        return String$1(argument);
    };

    // a string of all valid unicode whitespaces
    var whitespaces$2 = '\u0009\u000A\u000B\u000C\u000D\u0020\u00A0\u1680\u2000\u2001\u2002' +
        '\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u3000\u2028\u2029\uFEFF';

    var uncurryThis$1 = functionUncurryThis;
    var requireObjectCoercible = requireObjectCoercible$3;
    var toString$1 = toString$2;
    var whitespaces$1 = whitespaces$2;

    var replace = uncurryThis$1(''.replace);
    var whitespace = '[' + whitespaces$1 + ']';
    var ltrim = RegExp('^' + whitespace + whitespace + '*');
    var rtrim = RegExp(whitespace + whitespace + '*$');

    // `String.prototype.{ trim, trimStart, trimEnd, trimLeft, trimRight }` methods implementation
    var createMethod = function (TYPE) {
        return function ($this) {
            var string = toString$1(requireObjectCoercible($this));
            if (TYPE & 1) string = replace(string, ltrim, '');
            if (TYPE & 2) string = replace(string, rtrim, '');
            return string;
        };
    };

    var stringTrim = {
        // `String.prototype.{ trimLeft, trimStart }` methods
        // https://tc39.es/ecma262/#sec-string.prototype.trimstart
        start: createMethod(1),
        // `String.prototype.{ trimRight, trimEnd }` methods
        // https://tc39.es/ecma262/#sec-string.prototype.trimend
        end: createMethod(2),
        // `String.prototype.trim` method
        // https://tc39.es/ecma262/#sec-string.prototype.trim
        trim: createMethod(3)
    };

    var global$2 = global$q;
    var fails$2 = fails$c;
    var uncurryThis = functionUncurryThis;
    var toString = toString$2;
    var trim = stringTrim.trim;
    var whitespaces = whitespaces$2;

    var $parseInt$1 = global$2.parseInt;
    var Symbol$1 = global$2.Symbol;
    var ITERATOR = Symbol$1 && Symbol$1.iterator;
    var hex = /^[+-]?0x/i;
    var exec = uncurryThis(hex.exec);
    var FORCED$1 = $parseInt$1(whitespaces + '08') !== 8 || $parseInt$1(whitespaces + '0x16') !== 22
        // MS Edge 18- broken with boxed symbols
        || (ITERATOR && !fails$2(function () {
            $parseInt$1(Object(ITERATOR));
        }));

    // `parseInt` method
    // https://tc39.es/ecma262/#sec-parseint-string-radix
    var numberParseInt = FORCED$1 ? function parseInt(string, radix) {
        var S = trim(toString(string));
        return $parseInt$1(S, (radix >>> 0) || (exec(hex, S) ? 16 : 10));
    } : $parseInt$1;

    var $$2 = _export;
    var $parseInt = numberParseInt;

    // `parseInt` method
    // https://tc39.es/ecma262/#sec-parseint-string-radix
    $$2({global: true, forced: parseInt != $parseInt}, {
        parseInt: $parseInt
    });

    var fails$1 = fails$c;
    var wellKnownSymbol$1 = wellKnownSymbol$7;
    var V8_VERSION$1 = engineV8Version;

    var SPECIES = wellKnownSymbol$1('species');

    var arrayMethodHasSpeciesSupport$2 = function (METHOD_NAME) {
        // We can't use this feature detection in V8 since it causes
        // deoptimization and serious performance degradation
        // https://github.com/zloirock/core-js/issues/677
        return V8_VERSION$1 >= 51 || !fails$1(function () {
            var array = [];
            var constructor = array.constructor = {};
            constructor[SPECIES] = function () {
                return {foo: 1};
            };
            return array[METHOD_NAME](Boolean).foo !== 1;
        });
    };

    var $$1 = _export;
    var $filter = arrayIteration.filter;
    var arrayMethodHasSpeciesSupport$1 = arrayMethodHasSpeciesSupport$2;

    var HAS_SPECIES_SUPPORT = arrayMethodHasSpeciesSupport$1('filter');

    // `Array.prototype.filter` method
    // https://tc39.es/ecma262/#sec-array.prototype.filter
    // with adding support of @@species
    $$1({target: 'Array', proto: true, forced: !HAS_SPECIES_SUPPORT}, {
        filter: function filter(callbackfn /* , thisArg */) {
            return $filter(this, callbackfn, arguments.length > 1 ? arguments[1] : undefined);
        }
    });

    var toPropertyKey = toPropertyKey$3;
    var definePropertyModule = objectDefineProperty;
    var createPropertyDescriptor = createPropertyDescriptor$3;

    var createProperty$1 = function (object, key, value) {
        var propertyKey = toPropertyKey(key);
        if (propertyKey in object) definePropertyModule.f(object, propertyKey, createPropertyDescriptor(0, value));
        else object[propertyKey] = value;
    };

    var $ = _export;
    var global$1 = global$q;
    var fails = fails$c;
    var isArray = isArray$2;
    var isObject = isObject$7;
    var toObject = toObject$3;
    var lengthOfArrayLike = lengthOfArrayLike$3;
    var createProperty = createProperty$1;
    var arraySpeciesCreate = arraySpeciesCreate$2;
    var arrayMethodHasSpeciesSupport = arrayMethodHasSpeciesSupport$2;
    var wellKnownSymbol = wellKnownSymbol$7;
    var V8_VERSION = engineV8Version;

    var IS_CONCAT_SPREADABLE = wellKnownSymbol('isConcatSpreadable');
    var MAX_SAFE_INTEGER = 0x1FFFFFFFFFFFFF;
    var MAXIMUM_ALLOWED_INDEX_EXCEEDED = 'Maximum allowed index exceeded';
    var TypeError$1 = global$1.TypeError;

    // We can't use this feature detection in V8 since it causes
    // deoptimization and serious performance degradation
    // https://github.com/zloirock/core-js/issues/679
    var IS_CONCAT_SPREADABLE_SUPPORT = V8_VERSION >= 51 || !fails(function () {
        var array = [];
        array[IS_CONCAT_SPREADABLE] = false;
        return array.concat()[0] !== array;
    });

    var SPECIES_SUPPORT = arrayMethodHasSpeciesSupport('concat');

    var isConcatSpreadable = function (O) {
        if (!isObject(O)) return false;
        var spreadable = O[IS_CONCAT_SPREADABLE];
        return spreadable !== undefined ? !!spreadable : isArray(O);
    };

    var FORCED = !IS_CONCAT_SPREADABLE_SUPPORT || !SPECIES_SUPPORT;

    // `Array.prototype.concat` method
    // https://tc39.es/ecma262/#sec-array.prototype.concat
    // with adding support of @@isConcatSpreadable and @@species
    $({target: 'Array', proto: true, arity: 1, forced: FORCED}, {
        // eslint-disable-next-line no-unused-vars -- required for `.length`
        concat: function concat(arg) {
            var O = toObject(this);
            var A = arraySpeciesCreate(O, 0);
            var n = 0;
            var i, k, length, len, E;
            for (i = -1, length = arguments.length; i < length; i++) {
                E = i === -1 ? O : arguments[i];
                if (isConcatSpreadable(E)) {
                    len = lengthOfArrayLike(E);
                    if (n + len > MAX_SAFE_INTEGER) throw TypeError$1(MAXIMUM_ALLOWED_INDEX_EXCEEDED);
                    for (k = 0; k < len; k++, n++) if (k in E) createProperty(A, n, E[k]);
                } else {
                    if (n >= MAX_SAFE_INTEGER) throw TypeError$1(MAXIMUM_ALLOWED_INDEX_EXCEEDED);
                    createProperty(A, n++, E);
                }
            }
            A.length = n;
            return A;
        }
    });

    /**
     * @author: Dennis Hernández
     * @webSite: http://djhvscf.github.io/Blog
     * @update: https://github.com/wenzhixin
     * @version: v1.2.0
     */

    $__default["default"].akottr.dragtable.prototype._restoreState = function (persistObj) {
        var i = 0;

        for (var _i = 0, _Object$entries = Object.entries(persistObj); _i < _Object$entries.length; _i++) {
            var _Object$entries$_i = _slicedToArray(_Object$entries[_i], 2),
                field = _Object$entries$_i[0],
                value = _Object$entries$_i[1];

            var $th = this.originalTable.el.find("th[data-field=\"".concat(field, "\"]"));

            if (!$th.length) {
                i++;
                continue;
            }

            this.originalTable.startIndex = $th.prevAll().length + 1;
            this.originalTable.endIndex = parseInt(value, 10) + 1 - i;

            this._bubbleCols();
        }
    }; // From MDN site, https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/filter


    var filterFn = function filterFn() {
        if (!Array.prototype.filter) {
            Array.prototype.filter = function (fun
                                               /* , thisArg*/
            ) {
                if (this === undefined || this === null) {
                    throw new TypeError();
                }

                var t = Object(this);
                var len = t.length >>> 0;

                if (typeof fun !== 'function') {
                    throw new TypeError();
                }

                var res = [];
                var thisArg = arguments.length >= 2 ? arguments[1] : undefined;

                for (var i = 0; i < len; i++) {
                    if (i in t) {
                        var val = t[i]; // NOTE: Technically this should Object.defineProperty at
                        //       the next index, as push can be affected by
                        //       properties on Object.prototype and Array.prototype.
                        //       But this method's new, and collisions should be
                        //       rare, so use the more-compatible alternative.

                        if (fun.call(thisArg, val, i, t)) {
                            res.push(val);
                        }
                    }
                }

                return res;
            };
        }
    };

    $__default["default"].extend($__default["default"].fn.bootstrapTable.defaults, {
        reorderableColumns: false,
        maxMovingRows: 10,
        // eslint-disable-next-line no-unused-vars
        onReorderColumn: function onReorderColumn(headerFields) {
            return false;
        },
        dragaccept: null
    });
    $__default["default"].extend($__default["default"].fn.bootstrapTable.Constructor.EVENTS, {
        'reorder-column.bs.table': 'onReorderColumn'
    });
    $__default["default"].fn.bootstrapTable.methods.push('orderColumns');

    $__default["default"].BootstrapTable = /*#__PURE__*/function (_$$BootstrapTable) {
        _inherits(_class, _$$BootstrapTable);

        var _super = _createSuper(_class);

        function _class() {
            _classCallCheck(this, _class);

            return _super.apply(this, arguments);
        }

        _createClass(_class, [{
            key: "initHeader",
            value: function initHeader() {
                var _get2;

                for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
                    args[_key] = arguments[_key];
                }

                (_get2 = _get(_getPrototypeOf(_class.prototype), "initHeader", this)).call.apply(_get2, [this].concat(args));

                if (!this.options.reorderableColumns) {
                    return;
                }

                this.makeRowsReorderable();
            }
        }, {
            key: "_toggleColumn",
            value: function _toggleColumn() {
                var _get3;

                for (var _len2 = arguments.length, args = new Array(_len2), _key2 = 0; _key2 < _len2; _key2++) {
                    args[_key2] = arguments[_key2];
                }

                (_get3 = _get(_getPrototypeOf(_class.prototype), "_toggleColumn", this)).call.apply(_get3, [this].concat(args));

                if (!this.options.reorderableColumns) {
                    return;
                }

                this.makeRowsReorderable();
            }
        }, {
            key: "toggleView",
            value: function toggleView() {
                var _get4;

                for (var _len3 = arguments.length, args = new Array(_len3), _key3 = 0; _key3 < _len3; _key3++) {
                    args[_key3] = arguments[_key3];
                }

                (_get4 = _get(_getPrototypeOf(_class.prototype), "toggleView", this)).call.apply(_get4, [this].concat(args));

                if (!this.options.reorderableColumns) {
                    return;
                }

                if (this.options.cardView) {
                    return;
                }

                this.makeRowsReorderable();
            }
        }, {
            key: "resetView",
            value: function resetView() {
                var _get5;

                for (var _len4 = arguments.length, args = new Array(_len4), _key4 = 0; _key4 < _len4; _key4++) {
                    args[_key4] = arguments[_key4];
                }

                (_get5 = _get(_getPrototypeOf(_class.prototype), "resetView", this)).call.apply(_get5, [this].concat(args));

                if (!this.options.reorderableColumns) {
                    return;
                }

                this.makeRowsReorderable();
            }
        }, {
            key: "makeRowsReorderable",
            value: function makeRowsReorderable() {
                var _this = this;

                var order = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : null;

                try {
                    $__default["default"](this.$el).dragtable('destroy');
                } catch (e) {// do nothing
                }

                $__default["default"](this.$el).dragtable({
                    maxMovingRows: this.options.maxMovingRows,
                    dragaccept: this.options.dragaccept,
                    clickDelay: 200,
                    dragHandle: '.th-inner',
                    restoreState: order ? order : this.columnsSortOrder,
                    beforeStop: function beforeStop(table) {
                        var sortOrder = {};
                        table.el.find('th').each(function (i, el) {
                            sortOrder[$__default["default"](el).data('field')] = i;
                        });
                        _this.columnsSortOrder = sortOrder;

                        if (_this.options.cookie) {
                            _this.persistReorderColumnsState(_this);
                        }

                        var ths = [];
                        var formatters = [];
                        var columns = [];
                        var columnsHidden = [];
                        var columnIndex = -1;
                        var optionsColumns = [];

                        _this.$header.find('th:not(.detail)').each(function (i, el) {
                            ths.push($__default["default"](el).data('field'));
                            formatters.push($__default["default"](el).data('formatter'));
                        }); // Exist columns not shown


                        if (ths.length < _this.columns.length) {
                            columnsHidden = _this.columns.filter(function (column) {
                                return !column.visible;
                            });

                            for (var i = 0; i < columnsHidden.length; i++) {
                                ths.push(columnsHidden[i].field);
                                formatters.push(columnsHidden[i].formatter);
                            }
                        }

                        for (var _i2 = 0; _i2 < ths.length; _i2++) {
                            columnIndex = _this.fieldsColumnsIndex[ths[_i2]];

                            if (columnIndex !== -1) {
                                _this.fieldsColumnsIndex[ths[_i2]] = _i2;
                                _this.columns[columnIndex].fieldIndex = _i2;
                                columns.push(_this.columns[columnIndex]);
                            }
                        }

                        _this.columns = columns;
                        filterFn(); // Support <IE9

                        $__default["default"].each(_this.columns, function (i, column) {
                            var found = false;
                            var field = column.field;

                            _this.options.columns[0].filter(function (item) {
                                if (!found && item['field'] === field) {
                                    optionsColumns.push(item);
                                    found = true;
                                    return false;
                                }

                                return true;
                            });
                        });
                        _this.options.columns[0] = optionsColumns;
                        _this.header.fields = ths;
                        _this.header.formatters = formatters;

                        _this.initHeader();

                        _this.initToolbar();

                        _this.initSearchText();

                        _this.initBody();

                        _this.resetView();

                        _this.trigger('reorder-column', ths);
                    }
                });
            }
        }, {
            key: "orderColumns",
            value: function orderColumns(order) {
                this.columnsSortOrder = order;
                this.makeRowsReorderable();
            }
        }]);

        return _class;
    }($__default["default"].BootstrapTable);

}));
