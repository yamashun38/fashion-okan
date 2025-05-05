document.addEventListener("DOMContentLoaded", function () {
    const deleteBtn = document.getElementById("deleteItemBtn");
    const toggleEditBtn = document.getElementById("toggleEdit");
    const saveBtn = document.getElementById("saveButton");

    // 削除確認ポップアップ
    deleteBtn?.addEventListener("click", function (e) {
        e.preventDefault();
        const itemId = deleteBtn.getAttribute("data-id");
        if (confirm("本当に削除しますか？")) {
            window.location.href = `/delete-item/${itemId}`;
        }
    });

    // 編集モード切替に応じて削除ボタンの表示を制御
    toggleEditBtn?.addEventListener("click", function () {
        setTimeout(() => {
            const isEditing = !saveBtn.classList.contains("d-none");
            if (isEditing) {
                deleteBtn.classList.add("d-none");
            } else {
                deleteBtn.classList.remove("d-none");
            }
        }, 0);
    });
});
