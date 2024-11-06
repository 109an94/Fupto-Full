//버튼 클릭시 셀렉터
document.addEventListener('DOMContentLoaded', function() {
    const buttons = document.querySelectorAll('.btn-url');
    
    buttons.forEach(button => {
        button.addEventListener('click', function() {
            buttons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
        });
    });
});