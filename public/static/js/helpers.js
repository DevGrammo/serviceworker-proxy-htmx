function includeComputedHeight(id) {
    let style = window.getComputedStyle(document.getElementById(id), null);
    let h = style.getPropertyValue("height");
    return parseInt(h).toString()
}

function includeComputedWidth(id) {
    let style = window.getComputedStyle(document.getElementById(id), null);
    let w = style.getPropertyValue("width");
    return parseInt(w).toString()
}