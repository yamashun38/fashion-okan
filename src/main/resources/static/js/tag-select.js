document.addEventListener("DOMContentLoaded", function () {
    const tagButtons = document.querySelectorAll(".tag-button");
    const selectedTagIdsInput = document.getElementById("selectedTagIds");

    let selectedIds = [];

    tagButtons.forEach(button => {
        const tagId = button.dataset.id;
        const isSelected = button.dataset.selected === "true";

        // 最初に、btn-outline-primaryが付いてたら選択済み
        if (button.classList.contains("btn-outline-primary")) {
            selectedIds.push(tagId);
        }

        button.addEventListener("click", function () {
            if (selectedIds.includes(tagId)) {
                // 選択解除
                selectedIds = selectedIds.filter(id => id !== tagId);
                button.classList.remove("btn-outline-primary");
                button.classList.add("btn-outline-secondary");
            } else {
                // 選択
                selectedIds.push(tagId);
                button.classList.remove("btn-outline-secondary");
                button.classList.add("btn-outline-primary");
            }

            // hiddenに選択中IDをセット
            selectedTagIdsInput.value = selectedIds.join(",");
        });
    });

    // 初期値の反映
    selectedTagIdsInput.value = selectedIds.join(",");
});
