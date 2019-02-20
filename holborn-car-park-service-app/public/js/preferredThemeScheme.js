let storage_key = 'mode';
let preferredThemeScheme = initPrefersColorScheme();
if (localStorage.getItem(storage_key) === null) {
    localStorage.setItem(storage_key, preferredThemeScheme.scheme);
    console.log("init for " + localStorage.getItem(storage_key));
} else {
    console.log("else for " + localStorage.getItem(storage_key));
    if (preferredThemeScheme.hasNativeSupport) localStorage.setItem(storage_key, preferredThemeScheme.scheme);
    else {
        preferredThemeScheme.scheme = localStorage.getItem(storage_key)
    }
}

window.addEventListener('pageshow', function(event) {
   if(preferredThemeScheme){
       preferredThemeScheme.scheme = localStorage.getItem(storage_key)
   }
});

// this runs every time the preferred color scheme changes (by the OS or manually)
preferredThemeScheme.onChange = function () {
    // document.getElementById('preferred-color-scheme').lastChild.data = preferredThemeScheme.scheme;
    localStorage.setItem('mode', preferredThemeScheme.scheme);
};
// let’s run it now
preferredThemeScheme.onChange();