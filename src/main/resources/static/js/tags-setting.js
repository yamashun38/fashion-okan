document.addEventListener("DOMContentLoaded", function () {
    const toggleEditBtn = document.getElementById("toggleEdit");

    // タグ編集モード切替
    toggleEditBtn?.addEventListener("click", function (e) {
        e.preventDefault();
        const tagItems = document.querySelectorAll(".list-group-item");

        tagItems.forEach(item => {
            const span = item.querySelector(".tag-name");
            const input = item.querySelector(".tag-edit");
            const deleteBtn = item.querySelector(".delete-tag-btn");

            // 表示・非表示を切り替え
            span.classList.toggle("d-none");
            input.classList.toggle("d-none");
            deleteBtn.classList.toggle("d-none");
        });
    });

    // タグ削除処理
    const deleteButtons = document.querySelectorAll(".delete-tag-btn");
    deleteButtons.forEach(btn => {
        btn.addEventListener("click", function (e) {
            e.preventDefault();
            const tagId = btn.getAttribute("data-id");
            if (confirm("本当にこのタグを削除しますか？")) {
                window.location.href = `/delete-tag/${tagId}`;
            }
        });
    });
});
