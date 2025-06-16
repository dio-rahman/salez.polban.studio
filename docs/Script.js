const modal = document.getElementById('modal');
const learnMoreBtn = document.getElementById('learn-more');
const closeBtn = document.querySelector('.close');
const navLinks = document.querySelectorAll('.nav-link');

learnMoreBtn.addEventListener('click', () => {
    modal.style.display = 'block';
    setTimeout(() => {
        modal.style.opacity = '1';
        modal.classList.add('active');
    }, 10);
});

closeBtn.addEventListener('click', () => {
    modal.style.opacity = '0';
    modal.classList.remove('active');
    setTimeout(() => {
        modal.style.display = 'none';
    }, 400);
});

window.addEventListener('click', (e) => {
    if (e.target === modal) {
        modal.style.opacity = '0';
        modal.classList.remove('active');
        setTimeout(() => {
            modal.style.display = 'none';
        }, 400);
    }
});

navLinks.forEach(link => {
    link.addEventListener('click', (e) => {
        e.preventDefault();
        const targetId = link.getAttribute('href');
        const targetElement = document.querySelector(targetId);
        const headerOffset = 80;
        const elementPosition = targetElement.getBoundingClientRect().top;
        const offsetPosition = elementPosition + window.pageYOffset - headerOffset;

        window.scrollTo({
            top: offsetPosition,
            behavior: 'smooth'
        });
    });
});