(function() {
// Sidebar prompt listing and editing
let selectedPromptId = null;

function renderPromptSidebar(prompts) {
    const sidebarList = document.getElementById('promptSidebarList');
    sidebarList.innerHTML = '';
    prompts.forEach(prompt => {
        const li = document.createElement('li');
        li.className = 'prompt-item' + (prompt.id === selectedPromptId ? ' selected' : '');
        li.dataset.id = prompt.id;
        // Title
        const titleSpan = document.createElement('span');
        titleSpan.className = 'conversation-topic';
        titleSpan.textContent = prompt.id;
        // Description (wrapped)
        const descDiv = document.createElement('div');
        descDiv.className = 'prompt-desc';
        descDiv.textContent = prompt.description;
        descDiv.style.wordBreak = 'break-word';
        descDiv.style.fontSize = '0.95em';
        descDiv.style.color = '#555';
        descDiv.style.marginTop = '2px';
        // Meta information and delete button
        const metaDiv = document.createElement('div');
        metaDiv.className = 'conversation-meta';
        // Updated date
        const updatedDateSpan = document.createElement('span');
        updatedDateSpan.className = 'conversation-date';
        updatedDateSpan.textContent = prompt.updatedDate ? `Last updated: ${formatDate(prompt.updatedDate)}` : '';
        // Delete button
        const deleteBtn = document.createElement('button');
        deleteBtn.className = 'delete-conv-btn';
        deleteBtn.title = 'Delete prompt';
        deleteBtn.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m5 0V4a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2"/><line x1="10" y1="11" x2="10" y2="17"/><line x1="14" y1="11" x2="14" y2="17"/></svg>`;
        deleteBtn.onclick = function(e) {
            e.stopPropagation();
            showConfirmModal('Delete this prompt?', function() {
                deletePrompt(prompt.id);
            });
        };
        metaDiv.appendChild(updatedDateSpan);
        metaDiv.appendChild(deleteBtn);
        li.appendChild(titleSpan);
        li.appendChild(descDiv);
        li.appendChild(metaDiv);
        // Add click event for selection
        li.onclick = function(e) {
            // Prevent click if delete button is pressed
            if (e.target === deleteBtn || deleteBtn.contains(e.target)) return;
            selectedPromptId = prompt.id;
            renderPromptSidebar(prompts);
            renderPromptEditForm(prompt);
        };
        sidebarList.appendChild(li);
    });
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    const [dateAndTime] = dateStr.split('.');
    return dateAndTime.replace('T', ' ');
}

function renderPromptEditForm(prompt) {
    const mainArea = document.getElementById('promptMainArea');
    mainArea.innerHTML = `
        <h2>Edit Prompt</h2>
        <form id="editPromptForm">
            <label for="edit-id">Prompt ID:</label><br>
            <input type="text" id="edit-id" value="${prompt.id}" disabled><br>
            <label for="edit-description">Description:</label><br>
            <input type="text" id="edit-description" value="${prompt.description}" required><br>
            <label for="edit-content">Content:</label><br>
            <textarea id="edit-content" rows="6" required>${prompt.content}</textarea><br>
            <button type="submit">Save</button>
            <button type="button" id="cancelEditBtn">Cancel</button>
        </form>
    `;
    document.getElementById('editPromptForm').onsubmit = function(e) {
        e.preventDefault();
        const description = document.getElementById('edit-description').value;
        const content = document.getElementById('edit-content').value;
        fetch('/prompts/' + prompt.id, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ description, content })
        })
        .then(res => {
            if (!res.ok) return res.text().then(t => Promise.reject(t));
            return res.json();
        })
        .then(data => {
            loadPrompts();
        })
        .catch(err => {
            alert('Error updating prompt: ' + err);
        });
    };
    document.getElementById('cancelEditBtn').onclick = function() {
        mainArea.innerHTML = '';
    };
}

document.getElementById('newPromptBtn').onclick = function() {
    selectedPromptId = null;
    renderNewPromptForm();
};

function renderNewPromptForm() {
    const mainArea = document.getElementById('promptMainArea');
    mainArea.innerHTML = `
        <h2>New Prompt</h2>
        <form id="newPromptForm">
            <label for="new-id">Prompt ID:</label><br>
            <input type="text" id="new-id" required><br>
            <label for="new-description">Description:</label><br>
            <input type="text" id="new-description" required><br>
            <label for="new-content">Content:</label><br>
            <textarea id="new-content" rows="6" required></textarea><br>
            <button type="submit">Create</button>
            <button type="button" id="cancelNewBtn">Cancel</button>
        </form>
    `;
    document.getElementById('newPromptForm').onsubmit = function(e) {
        e.preventDefault();
        const id = document.getElementById('new-id').value.trim();
        const description = document.getElementById('new-description').value.trim();
        const content = document.getElementById('new-content').value.trim();
        if (!id || !description || !content) return;
        fetch('/prompts', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id, description, content })
        })
        .then(res => {
            if (!res.ok) return res.text().then(t => Promise.reject(t));
            return res.json();
        })
        .then(data => {
            loadPrompts();
        })
        .catch(err => {
            alert('Error creating prompt: ' + err);
        });
    };
    document.getElementById('cancelNewBtn').onclick = function() {
        mainArea.innerHTML = '';
    };
}

function loadPrompts() {
    fetch('/prompts')
        .then(res => res.json())
        .then(data => {
            renderPromptSidebar(data);
            // If a prompt is selected, show its edit form
            if (selectedPromptId) {
                const selected = data.find(p => p.id === selectedPromptId);
                if (selected) {
                    renderPromptEditForm(selected);
                } else {
                    document.getElementById('promptMainArea').innerHTML = '';
                }
            } else {
                document.getElementById('promptMainArea').innerHTML = '';
            }
        });
}

function deletePrompt(id) {
    fetch('/prompts/' + id, { method: 'DELETE' })
        .then(res => {
            if (!res.ok) return res.text().then(t => Promise.reject(t));
            selectedPromptId = null;
            loadPrompts();
        })
        .catch(err => {
            alert('Error deleting prompt: ' + err);
        });
}

function showConfirmModal(message, onConfirm) {
    // Reuse the modal from conversation page if present
    let modal = document.getElementById('confirmModal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'confirmModal';
        modal.style.display = 'none';
        modal.style.position = 'fixed';
        modal.style.top = '0';
        modal.style.left = '0';
        modal.style.width = '100vw';
        modal.style.height = '100vh';
        modal.style.background = 'rgba(0,0,0,0.3)';
        modal.style.zIndex = '9999';
        modal.style.alignItems = 'center';
        modal.style.justifyContent = 'center';
        modal.innerHTML = `
            <div style="background:#fff;padding:2em 2em;border-radius:8px;box-shadow:0 2px 16px rgba(0,0,0,0.2);min-width:300px;max-width:90vw;text-align:center;">
                <div id="confirmModalMsg" style="margin-bottom:1.5em;font-size:1.1em;"></div>
                <button id="confirmModalOk" style="background:#007bff;color:#fff;border:none;border-radius:4px;padding:0.5em 1.5em;margin-right:1em;cursor:pointer;">OK</button>
                <button id="confirmModalCancel" style="background:#888;color:#fff;border:none;border-radius:4px;padding:0.5em 1.5em;cursor:pointer;">Cancel</button>
            </div>
        `;
        document.body.appendChild(modal);
    }
    const msg = document.getElementById('confirmModalMsg');
    const okBtn = document.getElementById('confirmModalOk');
    const cancelBtn = document.getElementById('confirmModalCancel');
    msg.textContent = message;
    modal.style.display = 'flex';
    okBtn.focus();
    function cleanup() {
        modal.style.display = 'none';
        okBtn.onclick = null;
        cancelBtn.onclick = null;
        document.onkeydown = null;
    }
    okBtn.onclick = function() {
        cleanup();
        onConfirm();
    };
    cancelBtn.onclick = function() {
        cleanup();
    };
    document.onkeydown = function(e) {
        if (e.key === 'Escape') {
            cleanup();
        } else if (e.key === 'Enter') {
            cleanup();
            onConfirm();
        }
    };
}

// Initial load
loadPrompts();
})();
