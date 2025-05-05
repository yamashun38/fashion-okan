document.addEventListener("DOMContentLoaded", function () {
    const toggleBtn = document.getElementById("toggleSearch");
    const form = document.getElementById("searchForm");
    const searchBtn = document.getElementById("searchBtn");
    const resetBtn = document.getElementById("resetBtn");

    let isOpen = false;

    toggleBtn.addEventListener("click", function () {
        // form.classList.toggle("d-none");
        if (!isOpen) {
            form.classList.remove("d-none");
            setTimeout(() => {
                form.style.maxHeight = form.scrollHeight + "px";
                form.style.opacity = 1;
            }, 0); // 小さなディレイを入れる
            isOpen = true;
        } else {
            form.style.maxHeight = "0";
            form.style.opacity = 0;
            setTimeout(() => {
                form.classList.add("d-none");
            }, 500); // アニメーションに合わせて
            isOpen = false;
        }
    });

    searchBtn.addEventListener("click", function (e) {
        const formData = new FormData(form);
        let hasValue = false;

        for (let [_, value] of formData.entries()) {
            if (value.trim() !== "") {
                hasValue = true;
                break;
            }
        }
    });

    // リセットボタン処理
    resetBtn?.addEventListener("click", function () {
        const inputs = form.querySelectorAll("input");
        inputs.forEach(input => {
            if (input.type === "checkbox") {
                input.checked = false;
            } else {
                input.value = "";
            }
        });
    });
});

