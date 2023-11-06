(function (document, window) {
    // polyfill for when forEach is not supported e.g. IE11
    if ('NodeList' in window && !NodeList.prototype.forEach) {
        NodeList.prototype.forEach = function (callback, scope) {
            for (let i = 0; i < this.length; i++) {
                callback.call(scope || window, this[i], i, this)
            }
        }
    }

    document.querySelectorAll('a[href="#print-dialogue"]')
        .forEach(function(link) {
            link.addEventListener('click', function(event) {
                event.preventDefault();
                window.print();
            })
        })
})(document, window);