"object"!=typeof JSON&&(JSON={}),function(){"use strict";function f(e){return 10>e?"0"+e:e}function quote(e){return escapable.lastIndex=0,escapable.test(e)?'"'+e.replace(escapable,function(e){var t=meta[e];return"string"==typeof t?t:"\\u"+("0000"+e.charCodeAt(0).toString(16)).slice(-4)})+'"':'"'+e+'"'}function str(e,t){var a,r,n,o,s,i=gap,u=t[e];switch(u&&"object"==typeof u&&"function"==typeof u.toJSON&&(u=u.toJSON(e)),"function"==typeof rep&&(u=rep.call(t,e,u)),typeof u){case"string":return quote(u);case"number":return isFinite(u)?String(u):"null";case"boolean":case"null":return String(u);case"object":if(!u)return"null";if(gap+=indent,s=[],"[object Array]"===Object.prototype.toString.apply(u)){for(o=u.length,a=0;o>a;a+=1)s[a]=str(a,u)||"null";return n=0===s.length?"[]":gap?"[\n"+gap+s.join(",\n"+gap)+"\n"+i+"]":"["+s.join(",")+"]",gap=i,n}if(rep&&"object"==typeof rep)for(o=rep.length,a=0;o>a;a+=1)"string"==typeof rep[a]&&(r=rep[a],n=str(r,u),n&&s.push(quote(r)+(gap?": ":":")+n));else for(r in u)Object.prototype.hasOwnProperty.call(u,r)&&(n=str(r,u),n&&s.push(quote(r)+(gap?": ":":")+n));return n=0===s.length?"{}":gap?"{\n"+gap+s.join(",\n"+gap)+"\n"+i+"}":"{"+s.join(",")+"}",gap=i,n}}"function"!=typeof Date.prototype.toJSON&&(Date.prototype.toJSON=function(){return isFinite(this.valueOf())?this.getUTCFullYear()+"-"+f(this.getUTCMonth()+1)+"-"+f(this.getUTCDate())+"T"+f(this.getUTCHours())+":"+f(this.getUTCMinutes())+":"+f(this.getUTCSeconds())+"Z":null},String.prototype.toJSON=Number.prototype.toJSON=Boolean.prototype.toJSON=function(){return this.valueOf()});var cx=/[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,escapable=/[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,gap,indent,meta={"\b":"\\b","	":"\\t","\n":"\\n","\f":"\\f","\r":"\\r",'"':'\\"',"\\":"\\\\"},rep;"function"!=typeof JSON.stringify&&(JSON.stringify=function(e,t,a){var r;if(gap="",indent="","number"==typeof a)for(r=0;a>r;r+=1)indent+=" ";else"string"==typeof a&&(indent=a);if(rep=t,!t||"function"==typeof t||"object"==typeof t&&"number"==typeof t.length)return str("",{"":e});throw new Error("JSON.stringify")}),"function"!=typeof JSON.parse&&(JSON.parse=function(text,reviver){function walk(e,t){var a,r,n=e[t];if(n&&"object"==typeof n)for(a in n)Object.prototype.hasOwnProperty.call(n,a)&&(r=walk(n,a),void 0!==r?n[a]=r:delete n[a]);return reviver.call(e,t,n)}var j;if(text=String(text),cx.lastIndex=0,cx.test(text)&&(text=text.replace(cx,function(e){return"\\u"+("0000"+e.charCodeAt(0).toString(16)).slice(-4)})),/^[\],:{}\s]*$/.test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,"@").replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,"]").replace(/(?:^|:|,)(?:\s*\[)+/g,"")))return j=eval("("+text+")"),"function"==typeof reviver?walk({"":j},""):j;throw new SyntaxError("JSON.parse")})}(),function(e,t){"use strict";var a=e.History=e.History||{},r=e.jQuery;if("undefined"!=typeof a.Adapter)throw new Error("History.js Adapter has already been loaded...");a.Adapter={bind:function(e,t,a){r(e).bind(t,a)},trigger:function(e,t,a){r(e).trigger(t,a)},extractEventData:function(e,a,r){var n=a&&a.originalEvent&&a.originalEvent[e]||r&&r[e]||t;return n},onDomLoad:function(e){r(e)}},"undefined"!=typeof a.init&&a.init()}(window),function(e){"use strict";var t=e.document,a=e.setTimeout||a,r=e.clearTimeout||r,n=e.setInterval||n,o=e.History=e.History||{};if("undefined"!=typeof o.initHtml4)throw new Error("History.js HTML4 Support has already been loaded...");o.initHtml4=function(){return"undefined"!=typeof o.initHtml4.initialized?!1:(o.initHtml4.initialized=!0,o.enabled=!0,o.savedHashes=[],o.isLastHash=function(e){var t,a=o.getHashByIndex();return t=e===a},o.isHashEqual=function(e,t){return e=encodeURIComponent(e).replace(/%25/g,"%"),t=encodeURIComponent(t).replace(/%25/g,"%"),e===t},o.saveHash=function(e){return o.isLastHash(e)?!1:(o.savedHashes.push(e),!0)},o.getHashByIndex=function(e){var t=null;return t="undefined"==typeof e?o.savedHashes[o.savedHashes.length-1]:0>e?o.savedHashes[o.savedHashes.length+e]:o.savedHashes[e]},o.discardedHashes={},o.discardedStates={},o.discardState=function(e,t,a){var r,n=o.getHashByState(e);return r={discardedState:e,backState:a,forwardState:t},o.discardedStates[n]=r,!0},o.discardHash=function(e,t,a){var r={discardedHash:e,backState:a,forwardState:t};return o.discardedHashes[e]=r,!0},o.discardedState=function(e){var t,a=o.getHashByState(e);return t=o.discardedStates[a]||!1},o.discardedHash=function(e){var t=o.discardedHashes[e]||!1;return t},o.recycleState=function(e){var t=o.getHashByState(e);return o.discardedState(e)&&delete o.discardedStates[t],!0},o.emulated.hashChange&&(o.hashChangeInit=function(){o.checkerFunction=null;var a,r,s,i,u="",l=Boolean(o.getHash());return o.isInternetExplorer()?(a="historyjs-iframe",r=t.createElement("iframe"),r.setAttribute("id",a),r.setAttribute("src","#"),r.style.display="none",t.body.appendChild(r),r.contentWindow.document.open(),r.contentWindow.document.close(),s="",i=!1,o.checkerFunction=function(){if(i)return!1;i=!0;var t=o.getHash(),a=o.getHash(r.contentWindow.document);return t!==u?(u=t,a!==t&&(s=a=t,r.contentWindow.document.open(),r.contentWindow.document.close(),r.contentWindow.document.location.hash=o.escapeHash(t)),o.Adapter.trigger(e,"hashchange")):a!==s&&(s=a,l&&""===a?o.back():o.setHash(a,!1)),i=!1,!0}):o.checkerFunction=function(){var t=o.getHash()||"";return t!==u&&(u=t,o.Adapter.trigger(e,"hashchange")),!0},o.intervalList.push(n(o.checkerFunction,o.options.hashChangeInterval)),!0},o.Adapter.onDomLoad(o.hashChangeInit)),o.emulated.pushState&&(o.onHashChange=function(t){var a,r=t&&t.newURL||o.getLocationHref(),n=o.getHashByUrl(r),s=null,i=null;return o.isLastHash(n)?(o.busy(!1),!1):(o.doubleCheckComplete(),o.saveHash(n),n&&o.isTraditionalAnchor(n)?(o.Adapter.trigger(e,"anchorchange"),o.busy(!1),!1):(s=o.extractState(o.getFullUrl(n||o.getLocationHref()),!0),o.isLastSavedState(s)?(o.busy(!1),!1):(i=o.getHashByState(s),a=o.discardedState(s),a?(o.getHashByIndex(-2)===o.getHashByState(a.forwardState)?o.back(!1):o.forward(!1),!1):(o.pushState(s.data,s.title,encodeURI(s.url),!1),!0))))},o.Adapter.bind(e,"hashchange",o.onHashChange),o.pushState=function(t,a,r,n){if(r=encodeURI(r).replace(/%25/g,"%"),o.getHashByUrl(r))throw new Error("History.js does not support states with fragment-identifiers (hashes/anchors).");if(n!==!1&&o.busy())return o.pushQueue({scope:o,callback:o.pushState,args:arguments,queue:n}),!1;o.busy(!0);var s=o.createStateObject(t,a,r),i=o.getHashByState(s),u=o.getState(!1),l=o.getHashByState(u),c=o.getHash(),d=o.expectedStateId==s.id;return o.storeState(s),o.expectedStateId=s.id,o.recycleState(s),o.setTitle(s),i===l?(o.busy(!1),!1):(o.saveState(s),d||o.Adapter.trigger(e,"statechange"),!o.isHashEqual(i,c)&&!o.isHashEqual(i,o.getShortUrl(o.getLocationHref()))&&o.setHash(i,!1),o.busy(!1),!0)},o.replaceState=function(t,a,r,n){if(r=encodeURI(r).replace(/%25/g,"%"),o.getHashByUrl(r))throw new Error("History.js does not support states with fragment-identifiers (hashes/anchors).");if(n!==!1&&o.busy())return o.pushQueue({scope:o,callback:o.replaceState,args:arguments,queue:n}),!1;o.busy(!0);var s=o.createStateObject(t,a,r),i=o.getHashByState(s),u=o.getState(!1),l=o.getHashByState(u),c=o.getStateByIndex(-2);return o.discardState(u,s,c),i===l?(o.storeState(s),o.expectedStateId=s.id,o.recycleState(s),o.setTitle(s),o.saveState(s),o.Adapter.trigger(e,"statechange"),o.busy(!1)):o.pushState(s.data,s.title,s.url,!1),!0}),o.emulated.pushState&&o.getHash()&&!o.emulated.hashChange&&o.Adapter.onDomLoad(function(){o.Adapter.trigger(e,"hashchange")}),void 0)},"undefined"!=typeof o.init&&o.init()}(window),function(e,t){"use strict";var a=e.console||t,r=e.document,n=e.navigator,o=!1,s=e.setTimeout,i=e.clearTimeout,u=e.setInterval,l=e.clearInterval,c=e.JSON,d=e.alert,p=e.History=e.History||{},f=e.history;try{o=e.sessionStorage,o.setItem("TEST","1"),o.removeItem("TEST")}catch(h){o=!1}if(c.stringify=c.stringify||c.encode,c.parse=c.parse||c.decode,"undefined"!=typeof p.init)throw new Error("History.js Core has already been loaded...");p.init=function(){return"undefined"==typeof p.Adapter?!1:("undefined"!=typeof p.initCore&&p.initCore(),"undefined"!=typeof p.initHtml4&&p.initHtml4(),!0)},p.initCore=function(){if("undefined"!=typeof p.initCore.initialized)return!1;if(p.initCore.initialized=!0,p.options=p.options||{},p.options.hashChangeInterval=p.options.hashChangeInterval||100,p.options.safariPollInterval=p.options.safariPollInterval||500,p.options.doubleCheckInterval=p.options.doubleCheckInterval||500,p.options.disableSuid=p.options.disableSuid||!1,p.options.storeInterval=p.options.storeInterval||1e3,p.options.busyDelay=p.options.busyDelay||250,p.options.debug=p.options.debug||!1,p.options.initialTitle=p.options.initialTitle||r.title,p.options.html4Mode=p.options.html4Mode||!1,p.options.delayInit=p.options.delayInit||!1,p.intervalList=[],p.clearAllIntervals=function(){var e,t=p.intervalList;if("undefined"!=typeof t&&null!==t){for(e=0;e<t.length;e++)l(t[e]);p.intervalList=null}},p.debug=function(){(p.options.debug||!1)&&p.log.apply(p,arguments)},p.log=function(){var e,t,n,o,s,i="undefined"!=typeof a&&"undefined"!=typeof a.log&&"undefined"!=typeof a.log.apply,u=r.getElementById("log");for(i?(o=Array.prototype.slice.call(arguments),e=o.shift(),"undefined"!=typeof a.debug?a.debug.apply(a,[e,o]):a.log.apply(a,[e,o])):e="\n"+arguments[0]+"\n",t=1,n=arguments.length;n>t;++t){if(s=arguments[t],"object"==typeof s&&"undefined"!=typeof c)try{s=c.stringify(s)}catch(l){}e+="\n"+s+"\n"}return u?(u.value+=e+"\n-----\n",u.scrollTop=u.scrollHeight-u.clientHeight):i||d(e),!0},p.getInternetExplorerMajorVersion=function(){var e=p.getInternetExplorerMajorVersion.cached="undefined"!=typeof p.getInternetExplorerMajorVersion.cached?p.getInternetExplorerMajorVersion.cached:function(){for(var e=3,t=r.createElement("div"),a=t.getElementsByTagName("i");(t.innerHTML="<!--[if gt IE "+ ++e+"]><i></i><![endif]-->")&&a[0];);return e>4?e:!1}();return e},p.isInternetExplorer=function(){var e=p.isInternetExplorer.cached="undefined"!=typeof p.isInternetExplorer.cached?p.isInternetExplorer.cached:Boolean(p.getInternetExplorerMajorVersion());return e},p.emulated=p.options.html4Mode?{pushState:!0,hashChange:!0}:{pushState:!Boolean(e.history&&e.history.pushState&&e.history.replaceState&&!/ Mobile\/([1-7][a-z]|(8([abcde]|f(1[0-8]))))/i.test(n.userAgent)&&!/AppleWebKit\/5([0-2]|3[0-2])/i.test(n.userAgent)),hashChange:Boolean(!("onhashchange"in e||"onhashchange"in r)||p.isInternetExplorer()&&p.getInternetExplorerMajorVersion()<8)},p.enabled=!p.emulated.pushState,p.bugs={setHash:Boolean(!p.emulated.pushState&&"Apple Computer, Inc."===n.vendor&&/AppleWebKit\/5([0-2]|3[0-3])/.test(n.userAgent)),safariPoll:Boolean(!p.emulated.pushState&&"Apple Computer, Inc."===n.vendor&&/AppleWebKit\/5([0-2]|3[0-3])/.test(n.userAgent)),ieDoubleCheck:Boolean(p.isInternetExplorer()&&p.getInternetExplorerMajorVersion()<8),hashEscape:Boolean(p.isInternetExplorer()&&p.getInternetExplorerMajorVersion()<7)},p.isEmptyObject=function(e){for(var t in e)if(e.hasOwnProperty(t))return!1;return!0},p.cloneObject=function(e){var t,a;return e?(t=c.stringify(e),a=c.parse(t)):a={},a},p.getRootUrl=function(){var e=r.location.protocol+"//"+(r.location.hostname||r.location.host);return r.location.port&&(e+=":"+r.location.port),e+="/"},p.getBaseHref=function(){var e=r.getElementsByTagName("base"),t=null,a="";return 1===e.length&&(t=e[0],a=t.href.replace(/[^\/]+$/,"")),a=a.replace(/\/+$/,""),a&&(a+="/"),a},p.getBaseUrl=function(){var e=p.getBaseHref()||p.getBasePageUrl()||p.getRootUrl();return e},p.getPageUrl=function(){var e,t=p.getState(!1,!1),a=(t||{}).url||p.getLocationHref();return e=a.replace(/\/+$/,"").replace(/[^\/]+$/,function(e){return/\./.test(e)?e:e+"/"})},p.getBasePageUrl=function(){var e=p.getLocationHref().replace(/[#\?].*/,"").replace(/[^\/]+$/,function(e){return/[^\/]$/.test(e)?"":e}).replace(/\/+$/,"")+"/";return e},p.getFullUrl=function(e,t){var a=e,r=e.substring(0,1);return t="undefined"==typeof t?!0:t,/[a-z]+\:\/\//.test(e)||(a="/"===r?p.getRootUrl()+e.replace(/^\/+/,""):"#"===r?p.getPageUrl().replace(/#.*/,"")+e:"?"===r?p.getPageUrl().replace(/[\?#].*/,"")+e:t?p.getBaseUrl()+e.replace(/^(\.\/)+/,""):p.getBasePageUrl()+e.replace(/^(\.\/)+/,"")),a.replace(/\#$/,"")},p.getShortUrl=function(e){var t=e,a=p.getBaseUrl(),r=p.getRootUrl();return p.emulated.pushState&&(t=t.replace(a,"")),t=t.replace(r,"/"),p.isTraditionalAnchor(t)&&(t="./"+t),t=t.replace(/^(\.\/)+/g,"./").replace(/\#$/,"")},p.getLocationHref=function(e){return e=e||r,e.URL===e.location.href?e.location.href:e.location.href===decodeURIComponent(e.URL)?e.URL:e.location.hash&&decodeURIComponent(e.location.href.replace(/^[^#]+/,""))===e.location.hash?e.location.href:-1==e.URL.indexOf("#")&&-1!=e.location.href.indexOf("#")?e.location.href:e.URL||e.location.href},p.store={},p.idToState=p.idToState||{},p.stateToId=p.stateToId||{},p.urlToId=p.urlToId||{},p.storedStates=p.storedStates||[],p.savedStates=p.savedStates||[],p.normalizeStore=function(){p.store.idToState=p.store.idToState||{},p.store.urlToId=p.store.urlToId||{},p.store.stateToId=p.store.stateToId||{}},p.getState=function(e,t){"undefined"==typeof e&&(e=!0),"undefined"==typeof t&&(t=!0);var a=p.getLastSavedState();return!a&&t&&(a=p.createStateObject()),e&&(a=p.cloneObject(a),a.url=a.cleanUrl||a.url),a},p.getIdByState=function(e){var t,a=p.extractId(e.url);if(!a)if(t=p.getStateString(e),"undefined"!=typeof p.stateToId[t])a=p.stateToId[t];else if("undefined"!=typeof p.store.stateToId[t])a=p.store.stateToId[t];else{for(;a=(new Date).getTime()+String(Math.random()).replace(/\D/g,""),"undefined"!=typeof p.idToState[a]||"undefined"!=typeof p.store.idToState[a];);p.stateToId[t]=a,p.idToState[a]=e}return a},p.normalizeState=function(e){var t,a;return e&&"object"==typeof e||(e={}),"undefined"!=typeof e.normalized?e:(e.data&&"object"==typeof e.data||(e.data={}),t={},t.normalized=!0,t.title=e.title||"",t.url=p.getFullUrl(e.url?e.url:p.getLocationHref()),t.hash=p.getShortUrl(t.url),t.data=p.cloneObject(e.data),t.id=p.getIdByState(t),t.cleanUrl=t.url.replace(/\??\&_suid.*/,""),t.url=t.cleanUrl,a=!p.isEmptyObject(t.data),(t.title||a)&&p.options.disableSuid!==!0&&(t.hash=p.getShortUrl(t.url).replace(/\??\&_suid.*/,""),/\?/.test(t.hash)||(t.hash+="?"),t.hash+="&_suid="+t.id),t.hashedUrl=p.getFullUrl(t.hash),(p.emulated.pushState||p.bugs.safariPoll)&&p.hasUrlDuplicate(t)&&(t.url=t.hashedUrl),t)},p.createStateObject=function(e,t,a){var r={data:e,title:t,url:a};return r=p.normalizeState(r)},p.getStateById=function(e){e=String(e);var a=p.idToState[e]||p.store.idToState[e]||t;return a},p.getStateString=function(e){var t,a,r;return t=p.normalizeState(e),a={data:t.data,title:e.title,url:e.url},r=c.stringify(a)},p.getStateId=function(e){var t,a;return t=p.normalizeState(e),a=t.id},p.getHashByState=function(e){var t,a;return t=p.normalizeState(e),a=t.hash},p.extractId=function(e){var t,a,r,n;return n=-1!=e.indexOf("#")?e.split("#")[0]:e,a=/(.*)\&_suid=([0-9]+)$/.exec(n),r=a?a[1]||e:e,t=a?String(a[2]||""):"",t||!1},p.isTraditionalAnchor=function(e){var t=!/[\/\?\.]/.test(e);return t},p.extractState=function(e,t){var a,r,n=null;return t=t||!1,a=p.extractId(e),a&&(n=p.getStateById(a)),n||(r=p.getFullUrl(e),a=p.getIdByUrl(r)||!1,a&&(n=p.getStateById(a)),!n&&t&&!p.isTraditionalAnchor(e)&&(n=p.createStateObject(null,null,r))),n},p.getIdByUrl=function(e){var a=p.urlToId[e]||p.store.urlToId[e]||t;return a},p.getLastSavedState=function(){return p.savedStates[p.savedStates.length-1]||t},p.getLastStoredState=function(){return p.storedStates[p.storedStates.length-1]||t},p.hasUrlDuplicate=function(e){var t,a=!1;return t=p.extractState(e.url),a=t&&t.id!==e.id},p.storeState=function(e){return p.urlToId[e.url]=e.id,p.storedStates.push(p.cloneObject(e)),e},p.isLastSavedState=function(e){var t,a,r,n=!1;return p.savedStates.length&&(t=e.id,a=p.getLastSavedState(),r=a.id,n=t===r),n},p.saveState=function(e){return p.isLastSavedState(e)?!1:(p.savedStates.push(p.cloneObject(e)),!0)},p.getStateByIndex=function(e){var t=null;return t="undefined"==typeof e?p.savedStates[p.savedStates.length-1]:0>e?p.savedStates[p.savedStates.length+e]:p.savedStates[e]},p.getCurrentIndex=function(){var e=null;return e=p.savedStates.length<1?0:p.savedStates.length-1},p.getHash=function(e){var t,a=p.getLocationHref(e);return t=p.getHashByUrl(a)},p.unescapeHash=function(e){var t=p.normalizeHash(e);return t=decodeURIComponent(t)},p.normalizeHash=function(e){var t=e.replace(/[^#]*#/,"").replace(/#.*/,"");return t},p.setHash=function(e,t){var a,n;return t!==!1&&p.busy()?(p.pushQueue({scope:p,callback:p.setHash,args:arguments,queue:t}),!1):(p.busy(!0),a=p.extractState(e,!0),a&&!p.emulated.pushState?p.pushState(a.data,a.title,a.url,!1):p.getHash()!==e&&(p.bugs.setHash?(n=p.getPageUrl(),p.pushState(null,null,n+"#"+e,!1)):r.location.hash=e),p)},p.escapeHash=function(t){var a=p.normalizeHash(t);return a=e.encodeURIComponent(a),p.bugs.hashEscape||(a=a.replace(/\%21/g,"!").replace(/\%26/g,"&").replace(/\%3D/g,"=").replace(/\%3F/g,"?")),a},p.getHashByUrl=function(e){var t=String(e).replace(/([^#]*)#?([^#]*)#?(.*)/,"$2");return t=p.unescapeHash(t)},p.setTitle=function(e){var t,a=e.title;a||(t=p.getStateByIndex(0),t&&t.url===e.url&&(a=t.title||p.options.initialTitle));try{r.getElementsByTagName("title")[0].innerHTML=a.replace("<","&lt;").replace(">","&gt;").replace(" & "," &amp; ")}catch(n){}return r.title=a,p},p.queues=[],p.busy=function(e){if("undefined"!=typeof e?p.busy.flag=e:"undefined"==typeof p.busy.flag&&(p.busy.flag=!1),!p.busy.flag){i(p.busy.timeout);var t=function(){var e,a,r;if(!p.busy.flag)for(e=p.queues.length-1;e>=0;--e)a=p.queues[e],0!==a.length&&(r=a.shift(),p.fireQueueItem(r),p.busy.timeout=s(t,p.options.busyDelay))};p.busy.timeout=s(t,p.options.busyDelay)}return p.busy.flag},p.busy.flag=!1,p.fireQueueItem=function(e){return e.callback.apply(e.scope||p,e.args||[])},p.pushQueue=function(e){return p.queues[e.queue||0]=p.queues[e.queue||0]||[],p.queues[e.queue||0].push(e),p},p.queue=function(e,t){return"function"==typeof e&&(e={callback:e}),"undefined"!=typeof t&&(e.queue=t),p.busy()?p.pushQueue(e):p.fireQueueItem(e),p},p.clearQueue=function(){return p.busy.flag=!1,p.queues=[],p},p.stateChanged=!1,p.doubleChecker=!1,p.doubleCheckComplete=function(){return p.stateChanged=!0,p.doubleCheckClear(),p},p.doubleCheckClear=function(){return p.doubleChecker&&(i(p.doubleChecker),p.doubleChecker=!1),p},p.doubleCheck=function(e){return p.stateChanged=!1,p.doubleCheckClear(),p.bugs.ieDoubleCheck&&(p.doubleChecker=s(function(){return p.doubleCheckClear(),p.stateChanged||e(),!0},p.options.doubleCheckInterval)),p},p.safariStatePoll=function(){var t,a=p.extractState(p.getLocationHref());return p.isLastSavedState(a)?void 0:(t=a,t||(t=p.createStateObject()),p.Adapter.trigger(e,"popstate"),p)},p.back=function(e){return e!==!1&&p.busy()?(p.pushQueue({scope:p,callback:p.back,args:arguments,queue:e}),!1):(p.busy(!0),p.doubleCheck(function(){p.back(!1)}),f.go(-1),!0)},p.forward=function(e){return e!==!1&&p.busy()?(p.pushQueue({scope:p,callback:p.forward,args:arguments,queue:e}),!1):(p.busy(!0),p.doubleCheck(function(){p.forward(!1)}),f.go(1),!0)},p.go=function(e,t){var a;if(e>0)for(a=1;e>=a;++a)p.forward(t);else{if(!(0>e))throw new Error("History.go: History.go requires a positive or negative integer passed.");for(a=-1;a>=e;--a)p.back(t)}return p},p.emulated.pushState){var h=function(){};p.pushState=p.pushState||h,p.replaceState=p.replaceState||h}else p.onPopState=function(t,a){var r,n,o=!1,s=!1;return p.doubleCheckComplete(),r=p.getHash(),r?(n=p.extractState(r||p.getLocationHref(),!0),n?p.replaceState(n.data,n.title,n.url,!1):(p.Adapter.trigger(e,"anchorchange"),p.busy(!1)),p.expectedStateId=!1,!1):(o=p.Adapter.extractEventData("state",t,a)||!1,s=o?p.getStateById(o):p.expectedStateId?p.getStateById(p.expectedStateId):p.extractState(p.getLocationHref()),s||(s=p.createStateObject(null,null,p.getLocationHref())),p.expectedStateId=!1,p.isLastSavedState(s)?(p.busy(!1),!1):(p.storeState(s),p.saveState(s),p.setTitle(s),p.Adapter.trigger(e,"statechange"),p.busy(!1),!0))},p.Adapter.bind(e,"popstate",p.onPopState),p.pushState=function(t,a,r,n){if(p.getHashByUrl(r)&&p.emulated.pushState)throw new Error("History.js does not support states with fragement-identifiers (hashes/anchors).");if(n!==!1&&p.busy())return p.pushQueue({scope:p,callback:p.pushState,args:arguments,queue:n}),!1;p.busy(!0);var o=p.createStateObject(t,a,r);return p.isLastSavedState(o)?p.busy(!1):(p.storeState(o),p.expectedStateId=o.id,f.pushState(o.id,o.title,o.url),p.Adapter.trigger(e,"popstate")),!0},p.replaceState=function(t,a,r,n){if(p.getHashByUrl(r)&&p.emulated.pushState)throw new Error("History.js does not support states with fragement-identifiers (hashes/anchors).");if(n!==!1&&p.busy())return p.pushQueue({scope:p,callback:p.replaceState,args:arguments,queue:n}),!1;p.busy(!0);var o=p.createStateObject(t,a,r);return p.isLastSavedState(o)?p.busy(!1):(p.storeState(o),p.expectedStateId=o.id,f.replaceState(o.id,o.title,o.url),p.Adapter.trigger(e,"popstate")),!0};if(o){try{p.store=c.parse(o.getItem("History.store"))||{}}catch(g){p.store={}}p.normalizeStore()}else p.store={},p.normalizeStore();p.Adapter.bind(e,"unload",p.clearAllIntervals),p.saveState(p.storeState(p.extractState(p.getLocationHref(),!0))),o&&(p.onUnload=function(){var e,t,a;try{e=c.parse(o.getItem("History.store"))||{}}catch(r){e={}}e.idToState=e.idToState||{},e.urlToId=e.urlToId||{},e.stateToId=e.stateToId||{};for(t in p.idToState)p.idToState.hasOwnProperty(t)&&(e.idToState[t]=p.idToState[t]);for(t in p.urlToId)p.urlToId.hasOwnProperty(t)&&(e.urlToId[t]=p.urlToId[t]);for(t in p.stateToId)p.stateToId.hasOwnProperty(t)&&(e.stateToId[t]=p.stateToId[t]);p.store=e,p.normalizeStore(),a=c.stringify(e);try{o.setItem("History.store",a)}catch(n){if(n.code!==DOMException.QUOTA_EXCEEDED_ERR)throw n;o.length&&(o.removeItem("History.store"),o.setItem("History.store",a))}},p.intervalList.push(u(p.onUnload,p.options.storeInterval)),p.Adapter.bind(e,"beforeunload",p.onUnload),p.Adapter.bind(e,"unload",p.onUnload)),p.emulated.pushState||(p.bugs.safariPoll&&p.intervalList.push(u(p.safariStatePoll,p.options.safariPollInterval)),("Apple Computer, Inc."===n.vendor||"Mozilla"===(n.appCodeName||""))&&(p.Adapter.bind(e,"hashchange",function(){p.Adapter.trigger(e,"popstate")}),p.getHash()&&p.Adapter.onDomLoad(function(){p.Adapter.trigger(e,"hashchange")})))},(!p.options||!p.options.delayInit)&&p.init()}(window);