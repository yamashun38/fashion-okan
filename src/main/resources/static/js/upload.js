document.addEventListener("DOMContentLoaded", function () {
    
    const fileInput = document.getElementById("file");
    const fileNameInput = document.getElementById("fileName");

    fileInput.addEventListener("change", function () {
        if (this.files.length > 0) {
            const file = this.files[0];
            const allowedExtensions = ["png", "jpg", "jpeg", "heic"];
            const fileExtension = file.name.split(".").pop().toLowerCase();

            // 拡張子チェック
            if (!allowedExtensions.includes(fileExtension)) {
                alert("許可されていないファイル形式です。png, jpg, jpeg, heic のみアップロード可能です。");
                this.value = ""; // ファイル選択をリセット
                fileNameInput.value = ""; // ファイル名もクリア
            } else {
                fileNameInput.value = file.name; // hidden フィールドにファイル名を格納
            }
        }
    });
});
