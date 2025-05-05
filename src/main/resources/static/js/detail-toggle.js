document.addEventListener("DOMContentLoaded", function () {
    const toggleButton = document.getElementById("toggleEdit");
    const saveButton = document.getElementById("saveButton");
    const inputs = document.querySelectorAll("input.form-control");
    const checkboxInput = document.getElementById("favorite");
    const selectedTagsView = document.getElementById("selectedTagsView");
    const allTagsView = document.getElementById("allTagsView");
    const icon = toggleButton.querySelector("i");
    const tagButtons = document.querySelectorAll("#allTagsView .tag-button");
    const selectedTagNames = Array.from(selectedTagsView.querySelectorAll("span")).map(span => span.textContent.trim());

    let isEditing = false;
    let originalValues = [];
    let originalChecked = false;

    toggleButton.addEventListener("click", function (e) {
        e.preventDefault();

        if (!isEditing) {
            // 編集モード ON
            originalValues = [];
            inputs.forEach(input => {
                // 初期値を記録
                originalValues.push(input.value);
                input.disabled = false;
            });

            // チェックボックスの初期状態を記録
            originalChecked = checkboxInput.checked;

            // チェックボックスを有効化
            checkboxInput.disabled = false;

            // タグ表示切り替え
            selectedTagsView.classList.add("d-none");
            allTagsView.classList.remove("d-none");

            // 選択済みのタグだけbtn-outline-primaryにする
            tagButtons.forEach(button => {
                const tagName = button.textContent.trim();
                if (selectedTagNames.includes(tagName)) {
                    button.classList.remove("btn-outline-secondary");
                    button.classList.add("btn-outline-primary");
                } else {
                    button.classList.remove("btn-outline-primary");
                    button.classList.add("btn-outline-secondary");
                }
            });

            // 登録ボタン表示
            saveButton.classList.remove("d-none");
            icon.classList.replace("bi-pencil", "bi-x-lg");
            icon.setAttribute("title", "キャンセル");
            isEditing = true;

        } else {
            // 編集モード OFF
            inputs.forEach((input, index) => {
                // 値を戻す
                input.value = originalValues[index];
                input.disabled = true;
            });

            // チェックボックスの状態を戻す
            checkboxInput.checked = originalChecked;

            // チェックボックスも無効化
            checkboxInput.disabled = true;

            // タグ表示切り替え
            selectedTagsView.classList.remove("d-none");
            allTagsView.classList.add("d-none");

            // 登録ボタン非表示
            saveButton.classList.add("d-none");
            icon.classList.replace("bi-x-lg", "bi-pencil");
            icon.setAttribute("title", "登録情報変更");
            isEditing = false;
        }
    });
});

