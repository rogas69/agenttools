// SPA loader for static HTML pages
const mainContent = document.getElementById('mainContent');
const loader = document.getElementById('loader');
const menuConv = document.getElementById('menu-conv');
const menuPrompts = document.getElementById('menu-prompts');

function setActive(menuBtn) {
    menuConv.classList.remove('active');
    menuPrompts.classList.remove('active');
    menuBtn.classList.add('active');
}

function loadScript(src, callback) {
    const script = document.createElement('script');
    script.src = src;
    script.onload = callback;
    document.body.appendChild(script);
}

function loadScriptsSequential(scripts, finalCallback) {
    if (scripts.length === 0) {
        finalCallback();
        return;
    }
    loadScript(scripts[0], () => loadScriptsSequential(scripts.slice(1), finalCallback));
}

function loadPage(page) {
    loader.style.display = 'block';
    // Remove the old mainContent container
    const oldContentDiv = document.getElementById('mainContent');
    if (oldContentDiv) {
        oldContentDiv.remove();
    }
    // Create a new mainContent container
    const newContentDiv = document.createElement('div');
    newContentDiv.id = 'mainContent';
    // For conversation.html, add .spa-mode
    if (page === 'conversation.html') {
        newContentDiv.classList.add('spa-mode');
    }
    // Insert after navbar
    const navbar = document.querySelector('.navbar');
    if (navbar && navbar.nextSibling) {
        navbar.parentNode.insertBefore(newContentDiv, navbar.nextSibling);
    } else if (navbar) {
        navbar.parentNode.appendChild(newContentDiv);
    }
    newContentDiv.innerHTML = loader.outerHTML;
    fetch(page)
        .then(res => res.text())
        .then(html => {
            // Extract only the body content
            const bodyMatch = html.match(/<body[^>]*>([\s\S]*)<\/body>/i);
            newContentDiv.innerHTML = bodyMatch ? bodyMatch[1] : html;
            if (page === 'conversation.html') {
                // Ensure CSS is loaded
                if (!document.querySelector('link[href="css/styles.css"]')) {
                    const link = document.createElement('link');
                    link.rel = 'stylesheet';
                    link.href = 'css/styles.css';
                    document.head.appendChild(link);
                }
                // Dynamically load purify.min.js, marked.umd.js, then conversation.js and initialize
                loadScriptsSequential([
                    'js/purify.min.js',
                    'js/marked.umd.js',
                    'js/conversation.js'
                ], function() {
                    if (typeof initConversationPage === 'function') {
                        initConversationPage();
                    }
                });
            } else if (page === 'prompts.html') {
                loadScript('js/prompts.js', function() {
                    if (typeof initPromptsPage === 'function') {
                        initPromptsPage();
                    }
                });
            }
        })
        .catch(() => {
            newContentDiv.innerHTML = '<div style="color:red;text-align:center;">Failed to load page.</div>';
        });
}

menuConv.onclick = function() {
    setActive(menuConv);
    loadPage('conversation.html');
};
menuPrompts.onclick = function() {
    setActive(menuPrompts);
    loadPage('prompts.html');
};

// Initial load: Conversations
loadPage('conversation.html');
