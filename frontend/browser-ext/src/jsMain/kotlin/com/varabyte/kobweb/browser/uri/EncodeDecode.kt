package com.varabyte.kobweb.browser.uri

// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI
external fun encodeURI(uri: String): String

// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent
external fun encodeURIComponent(uriComponent: String): String

// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI
external fun decodeURI(encodedURI: String): String

// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent
external fun decodeURIComponent(encodedURIComponent: String): String