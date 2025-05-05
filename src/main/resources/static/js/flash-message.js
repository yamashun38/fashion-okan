document.addEventListener("DOMContentLoaded", function () {
    const flash = document.getElementById("flashMessage");
    if (flash) {
        flash.classList.remove("d-none");

        // フェードイン
        setTimeout(() => {
            flash.classList.add("show");
        }, 100);

        // 2秒後にフェードアウト + 非表示
        setTimeout(() => {
            flash.classList.remove("show");
            setTimeout(() => {
                flash.remove();
            }, 500); // フェードアウト完了後に削除
        }, 2000);
    }
});
