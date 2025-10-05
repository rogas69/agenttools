(function() {
let selectedConversationId = null;

function fetchConversations() {
    fetch('/conversation')
        .then(res => res.json())
        .then(data => {
            const list = document.getElementById('conversationList');
            list.innerHTML = '';
            if (data.length === 0) {
                list.innerHTML = '<li class="empty">No conversations found.</li>';
                return;
            }
            data.forEach(conv => {
                const li = document.createElement('li');
                li.className = 'conversation-item' + (conv.id === selectedConversationId ? ' selected' : '');
                li.dataset.id = conv.id;
                // Conversation title area
                const topicSpan = document.createElement('span');
                topicSpan.className = 'conversation-topic';
                topicSpan.textContent = conv.topic || conv.title || conv.id;
                // Pencil icon for editing
                const editBtn = document.createElement('button');
                editBtn.className = 'edit-title-btn';
                editBtn.title = 'Edit title';
                editBtn.innerHTML = `<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='none' stroke='#007bff' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M12 20h9'/><path d='M17.5 3.5a2.121 2.121 0 0 1 3 3L7 20l-4 1 1-4 13.5-13.5z'/></svg>`;
                editBtn.style.background = 'none';
                editBtn.style.border = 'none';
                editBtn.style.cursor = 'pointer';
                editBtn.style.marginLeft = '6px';
                // Edit logic
                editBtn.onclick = function(e) {
                    e.stopPropagation();
                    // Replace topicSpan with input
                    const input = document.createElement('input');
                    input.type = 'text';
                    input.value = topicSpan.textContent;
                    input.className = 'edit-title-input';
                    input.style.width = '80%';
                    input.style.fontSize = '1em';
                    input.style.marginRight = '6px';
                    topicSpan.replaceWith(input);
                    editBtn.style.display = 'none';
                    input.focus();
                    // Handle Enter/Esc
                    input.onkeydown = function(ev) {
                        if (ev.key === 'Enter') {
                            const newTitle = input.value.trim();
                            if (newTitle && newTitle !== (conv.topic || conv.title || conv.id)) {
                                fetch(`/conversation/${conv.id}`, {
                                    method: 'PUT',
                                    headers: { 'Content-Type': 'application/json' },
                                    body: JSON.stringify({ title: newTitle })
                                })
                                .then(res => {
                                    if (res.ok) {
                                        topicSpan.textContent = newTitle;
                                        conv.topic = newTitle;
                                    }
                                    input.replaceWith(topicSpan);
                                    editBtn.style.display = '';
                                });
                            } else {
                                input.replaceWith(topicSpan);
                                editBtn.style.display = '';
                            }
                        } else if (ev.key === 'Escape') {
                            input.replaceWith(topicSpan);
                            editBtn.style.display = '';
                        }
                    };
                    // Blur also cancels
                    input.onblur = function() {
                        input.replaceWith(topicSpan);
                        editBtn.style.display = '';
                    };
                };
                // Meta information and delete button
                const metaDiv = document.createElement('div');
                metaDiv.className = 'conversation-meta';
                metaDiv.innerHTML = `<span class="conversation-date">Created: ${formatDate(conv.dateCreated)}</span>`;
                const deleteBtn = document.createElement('button');
                deleteBtn.className = 'delete-conv-btn';
                deleteBtn.title = 'Delete conversation';
                deleteBtn.innerHTML = `<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"18\" height=\"18\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#fff\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><polyline points=\"3 6 5 6 21 6\"/><path d=\"M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m5 0V4a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2\"/><line x1=\"10\" y1=\"11\" x2=\"10\" y2=\"17\"/><line x1=\"14\" y1=\"11\" x2=\"14\" y2=\"17\"/></svg>`;
                deleteBtn.onclick = function(event) {
                    event.stopPropagation();
                    showConfirmModal('Delete this conversation?', function() {
                        deleteConversation(conv.id);
                    });
                };
                metaDiv.appendChild(deleteBtn);
                topicSpan.appendChild(editBtn);
                li.appendChild(topicSpan);
                li.appendChild(metaDiv);
                li.onclick = (e) => {
                    if (e.target.classList.contains('delete-conv-btn') || e.target.classList.contains('edit-title-btn') || e.target.classList.contains('edit-title-input')) return;
                    selectedConversationId = conv.id;
                    highlightSelectedConversation();
                    fetchMessages(conv.id);
                };
                list.appendChild(li);
            });
        });
}

function formatDate(dateStr) {
    if (!dateStr) return 'unknown';
    const [dateAndTime] = dateStr.split('.');
    return dateAndTime.replace('T', ' ');
}

function highlightSelectedConversation() {
    const items = document.querySelectorAll('.conversation-item');
    items.forEach(item => {
        // Remove 'selected' from all items
        item.classList.remove('selected');
        // Add 'selected' only to the item whose data-id matches selectedConversationId
        if (item.dataset && item.dataset.id === String(selectedConversationId)) {
            item.classList.add('selected');
        }
    });
}

function fetchMessages(conversationId) {
    const messagesDiv = document.getElementById('messages');
    if (!messagesDiv) {
        console.warn('Messages container not found in DOM.');
        return;
    }
    fetch(`/conversation/${conversationId}/messages`)
        .then(res => res.json())
        .then(data => {
            messagesDiv.innerHTML = '';
            if (data.length === 0) {
                messagesDiv.innerHTML = '<div class="empty">No messages yet.</div>';
                return;
            }
            data.forEach(msg => {
                const div = document.createElement('div');
                div.className = 'message ' + (msg.role === 'user' ? 'user' : 'assistant');
                div.innerHTML = `<strong>${msg.role === 'user' ? 'You' : 'Assistant'}:</strong><br>` + DOMPurify.sanitize(marked.parse(msg.content));
                messagesDiv.appendChild(div);
            });
            messagesDiv.scrollTop = messagesDiv.scrollHeight;
        });
}

// Send message
function setupConversationForm() {
    const form = document.getElementById('messageForm');
    const input = document.getElementById('messageInput');
    if (!form || !input) return;
    form.onsubmit = function(e) {
        e.preventDefault();
        sendMessage();
    };
    input.addEventListener('keydown', function(e) {
        if (e.ctrlKey && e.key === 'Enter') {
            e.preventDefault();
            sendMessage();
        }
    });
}

function sendMessage() {
    const input = document.getElementById('messageInput');
    const content = input.value.trim();
    if (!content || !selectedConversationId) return;
    // Display user message immediately
    const messagesDiv = document.getElementById('messages');
    const userDiv = document.createElement('div');
    userDiv.className = 'message user';
    userDiv.innerHTML = `<strong>You:</strong><br>` + DOMPurify.sanitize(marked.parse(content));
    messagesDiv.appendChild(userDiv);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
    // Show assistant indicator
    let indicatorDiv = document.getElementById('assistant-indicator');
    if (!indicatorDiv) {
        indicatorDiv = document.createElement('div');
        indicatorDiv.id = 'assistant-indicator';
        indicatorDiv.className = 'message assistant';
        indicatorDiv.innerHTML = '<em>Assistant is responding...</em>';
        messagesDiv.appendChild(indicatorDiv);
        messagesDiv.scrollTop = messagesDiv.scrollHeight;
    }
    fetch(`/conversation/${selectedConversationId}/messages`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ content })
    })
    .then(res => res.text())
    .then(_ => {
        input.value = '';
        // Remove indicator before loading new messages
        if (indicatorDiv) indicatorDiv.remove();
        fetchMessages(selectedConversationId);
    });
}

function setupConversationButtons() {
    const newConvBtn = document.getElementById('newConvBtn');
    const clearConvBtn = document.getElementById('clearConvBtn');
    if (newConvBtn) {
        newConvBtn.onclick = function() {
            fetch('/conversation', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ topic: 'New Conversation' })
            })
            .then(res => res.json())
            .then(conv => {
                selectedConversationId = conv.id;
                fetchConversations();
                fetchMessages(conv.id);
            });
        };
    }
    if (clearConvBtn) {
        clearConvBtn.onclick = function() {
            if (!selectedConversationId) return;
            showConfirmModal('Clear all messages in this conversation?', function() {
                fetch(`/conversation/${selectedConversationId}/clear`, {
                    method: 'PUT'
                })
                .then(res => {
                    if (res.ok) {
                        fetchMessages(selectedConversationId);
                        document.getElementById('messages').innerHTML = '<div class="empty">No messages yet.</div>';
                    }
                });
            });
        };
    }
}

function deleteConversation(conversationId) {
    showConfirmModal('Delete this conversation?', function() {
        fetch(`/conversation/${conversationId}`, {
            method: 'DELETE'
        })
        .then(res => {
            if (res.ok) {
                if (selectedConversationId === conversationId) {
                    selectedConversationId = null;
                    document.getElementById('messages').innerHTML = '';
                }
                fetchConversations();
            }
        });
    });
}

function showConfirmModal(message, onConfirm) {
    const modal = document.getElementById('confirmModal');
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

// SPA: initialize when content is loaded
function initConversationPage() {
    fetch('/conversation')
        .then(res => res.json())
        .then(data => {
            fetchConversations();
            setupConversationForm();
            setupConversationButtons();
            // Auto-select first conversation and show its messages
            if (data.length > 0) {
                selectedConversationId = data[0].id;
                highlightSelectedConversation();
                fetchMessages(selectedConversationId);
            } else {
                selectedConversationId = null;
                document.getElementById('messages').innerHTML = '<div class="empty">No messages yet.</div>';
            }
            // Optionally, refresh conversations every 30s
            if (window.conversationInterval) clearInterval(window.conversationInterval);
            window.conversationInterval = setInterval(fetchConversations, 30000);
        });
}

// If loaded directly, run initConversationPage on DOMContentLoaded
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initConversationPage);
} else {
    initConversationPage();
}
})();
