if (location.pathname === '/admin/category' || location.pathname === '/admin/discount') {
    const ul = document.getElementById('tags-ul'),
        input = document.getElementById('tags');
    /*Số lượng tối đa tag*/
    let maxTags = 10,
        /*Thiết lập tag mặc định*/
        tags = [];
    countTags();
    createTag();
    /*Đếm số lượng tag*/
    function countTags() {
        input.focus();
    }
    /*Tạo tag*/
    function createTag() {
        ul.querySelectorAll('li').forEach((li) => li.remove());
        tags.slice()
            .reverse()
            .forEach((tag) => {
                let liTag = `<li>${tag}<i class="bx bx-x" onclick="remove(this, 
'${tag}')"></i></li>`;
                ul.insertAdjacentHTML('afterbegin', liTag);
            });
        countTags();
    }
    /*Xoá tag*/
    function remove(element, tag) {
        let index = tags.indexOf(tag);
        tags = [...tags.slice(0, index), ...tags.slice(index + 1)];
        element.parentElement.remove();
        countTags();
    }
    /*Thêm tag bằng dấu enter*/
    function addTag(e) {
        if (e.key == 'Enter') {
            let tag = e.target.value.replace(/\s+/g, ' ');
            if (tag.length > 1 && !tags.includes(tag)) {
                if (tags.length < 10) {
                    tag.split(',').forEach((tag) => {
                        tags.push(tag);
                        createTag();
                    });
                }
            }
            e.target.value = '';
        }
    }
    input.addEventListener('keyup', addTag);

    function add(tag) {
        if (!tags.includes(tag)) {
            if (tags.length < 10) {
                tag.split(',').forEach((tag) => {
                    tags.push(tag);
                    createTag();
                });
            }
        }
    }

    function clearTags() {
        tags = [];
    }
}
