document.addEventListener('DOMContentLoaded', () => {
    const CART_STORAGE_KEY = 'smartshop.cart';
    let cart = restoreCart();
    const SITE_NAME = "SmartShop";

    function restoreCart() {
        try {
            const saved = JSON.parse(localStorage.getItem(CART_STORAGE_KEY) || '[]');
            if (!Array.isArray(saved)) return [];
            return saved.filter(item => item &&
                /^\d+$/.test(String(item.id)) &&
                typeof item.name === 'string' &&
                Number.isFinite(item.price) && item.price >= 0 &&
                Number.isInteger(item.count) && item.count > 0);
        } catch (error) {
            return [];
        }
    }

    // Элементы DOM
    const productsList = document.getElementById('products-list');
    const cartItemsContainer = document.getElementById('cart-items');
    const cartModal = document.getElementById('cartModal');
    const cartCount = document.getElementById('cart-count');
    const totalPrice = document.getElementById('total-price');
    const mainTitle = document.getElementById('main-title');
    const openCartBtn = document.getElementById('open-cart-btn');

    // Показ уведомлений
    function showToast(message) {
        const container = document.getElementById('toast-container');
        const toast = document.createElement('div');
        const toastAttributeElement = document.createElement('span');
        toastAttributeElement.setAttribute("dataTest", "notification-container");
        toastAttributeElement.hidden = true;
        toastAttributeElement.className = "toastContainer";
        toast.className = 'toast';
        toast.innerText = message;
        toast.appendChild(toastAttributeElement);
        container.appendChild(toast);
        setTimeout(() => toast.remove(), 5000);
    }

    /**
     * Универсальная логика добавления в корзину
     * Используется и при клике, и при Drag-and-Drop
     */
    function addToCartLogic(name, price, id, qty) {
        const item = cart.find(i => i.id == id);
        if (item) {
            item.count += qty;
        } else {
            cart.push({ name, price: Number(price), count: qty, id: id });
        }
        showToast(`${name} (${qty} шт.) добавлен в корзину`);
        renderCart();
    }

    // Загрузка товаров с сервера
    async function loadProducts() {
        try {
            const res = await fetch('/goods/list?page=0&size=20');
            const data = await res.json();

            productsList.innerHTML = data.goods.map(p => `
                <div class="product-card" id="card-${p.id}" draggable="true"
                     data-id="${p.id}" data-name="${p.name}" data-price="${p.price}">
                    <h4>${p.name}</h4>
                    <div style="color:var(--primary); font-weight:bold">${p.price} ₽</div>
                    <div class="qty-controls">
                        <button class="qty-btn" data-action="qty-change" data-id="${p.id}" data-step="-1">-</button>
                        <input type="number" id="q-${p.id}" class="qty-input" value="1" min="1">
                        <button class="qty-btn" data-action="qty-change" data-id="${p.id}" data-step="1">+</button>
                    </div>
                    <button class="btn" data-action="add-to-cart" data-name="${p.name}" data-price="${p.price}" data-id="${p.id}">В корзину</button>
                </div>
            `).join('');

            setupDragEvents(); // Инициализация событий перетаскивания
        } catch (e) {
            showToast("Ошибка загрузки товаров");
        }
    }

    // Настройка событий Drag-and-Drop для карточек
    function setupDragEvents() {
        const cards = document.querySelectorAll('.product-card');
        cards.forEach(card => {
            card.addEventListener('dragstart', (e) => {
                const { id, name, price } = e.target.dataset;
                // Передаем данные товара через JSON
                e.dataTransfer.setData('application/json', JSON.stringify({ id, name, price }));
                e.target.style.opacity = '0.5';
            });

            card.addEventListener('dragend', (e) => {
                e.target.style.opacity = '1';
            });
        });
    }

    // Настройка зоны сброса (Кнопка корзины)
    openCartBtn.addEventListener('dragover', (e) => {
        e.preventDefault(); // Разрешаем Drop
        openCartBtn.classList.add('drag-over');
    });

    openCartBtn.addEventListener('dragleave', () => {
        openCartBtn.classList.remove('drag-over');
    });

    openCartBtn.addEventListener('drop', (e) => {
        e.preventDefault();
        openCartBtn.classList.remove('drag-over');

        try {
            const data = JSON.parse(e.dataTransfer.getData('application/json'));
            const qtyInput = document.getElementById(`q-${data.id}`);
            const qty = qtyInput ? parseInt(qtyInput.value) : 1;

            addToCartLogic(data.name, data.price, data.id, qty);
        } catch (err) {
            console.error("Ошибка при обработке drop:", err);
        }
    });

    // Рендер корзины (Модальное окно)
    function renderCart() {
        cartItemsContainer.innerHTML = cart.length === 0 ? '<p id="empty-cart">Пусто</p>' :
            cart.map((item, i) => `
                <div class="cart-item" id="cart-item-${item.id}">
                    <div style="flex:1"><b>${item.name}</b></div>
                    <div class="qty-controls" style="margin:0 15px">
                        <button class="qty-btn" data-action="cart-qty" data-index="${i}" data-step="-1">-</button>
                        <span>${item.count}</span>
                        <button class="qty-btn" data-action="cart-qty" data-index="${i}" data-step="1">+</button>
                    </div>
                    <div style="width:70px; text-align:right">${item.price * item.count} ₽</div>
                    <button data-action="remove" data-index="${i}" style="border:none; background:none; color:red; cursor:pointer; margin-left:10px">✕</button>
                </div>
            `).join('');

        totalPrice.innerText = cart.reduce((s, i) => s + (i.price * i.count), 0);
        cartCount.innerText = cart.reduce((s, i) => s + i.count, 0);
        try {
            localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(cart));
        } catch (error) {
            console.warn('Не удалось сохранить корзину', error);
        }
    }

    // --- Делегирование событий ---

    // Список товаров (клики по кнопкам +/- и "В корзину")
    productsList.addEventListener('click', (e) => {
        const btn = e.target.closest('button');
        if (!btn) return;

        const { action, id, step, name, price } = btn.dataset;

        if (action === 'qty-change') {
            const input = document.getElementById(`q-${id}`);
            input.value = Math.max(1, parseInt(input.value) + parseInt(step));
        }

        if (action === 'add-to-cart') {
            const qty = parseInt(document.getElementById(`q-${id}`).value);
            addToCartLogic(name, price, id, qty);
        }
    });

    // Внутри корзины (клики по +/- и удалить)
    cartItemsContainer.addEventListener('click', (e) => {
        const btn = e.target.closest('button');
        if (!btn) return;

        const { action, index, step } = btn.dataset;
        const idx = parseInt(index);

        if (action === 'cart-qty') {
            cart[idx].count = Math.max(1, cart[idx].count + parseInt(step));
        }

        if (action === 'remove') {
            cart.splice(idx, 1);
        }
        renderCart();
    });

    // Управление модальным окном
    openCartBtn.addEventListener('click', () => cartModal.style.display = 'block');
    document.getElementById('close-modal').addEventListener('click', () => cartModal.style.display = 'none');
    window.addEventListener('click', (e) => { if (e.target === cartModal) cartModal.style.display = 'none'; });

    // Оформление заказа
    document.getElementById('makeOrder').addEventListener('click', () => {
        const total = parseInt(totalPrice.innerText);
        if (total === 0) return;

        if (total > 300) {
            alert(`[${SITE_NAME}]: Денег не хватает! Сумма ${total} ₽ превышает лимит 300 ₽.`);
        } else {
            cart = [];
            renderCart();
            cartModal.style.display = 'none';
            mainTitle.innerText = "Заказ успешно оформлен!";
            showToast("Заказ принят в обработку!");
        }
    });

    renderCart();
    loadProducts();
});
