// =====================
// NAVBAR SCROLL EFFECT
// =====================
const navbar = document.getElementById('navbar');
window.addEventListener('scroll', () => {
    if (window.scrollY > 20) {
        navbar.classList.add('scrolled');
    } else {
        navbar.classList.remove('scrolled');
    }
});

// =====================
// HAMBURGER MENU
// =====================
const hamburger = document.getElementById('hamburger');
const navLinks = document.querySelector('.nav-links');

hamburger.addEventListener('click', () => {
    navLinks.classList.toggle('open');
});

// Close menu when a link is clicked
navLinks.querySelectorAll('a').forEach(link => {
    link.addEventListener('click', () => {
        navLinks.classList.remove('open');
    });
});

// =====================
// WINDOW TABS INTERACTION
// =====================
const tabs = document.querySelectorAll('.tab');
tabs.forEach(tab => {
    tab.addEventListener('click', () => {
        tabs.forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
    });
});

// =====================
// SCROLL ANIMATIONS (Intersection Observer)
// =====================
const animateOnScroll = (selector, className = 'visible') => {
    const elements = document.querySelectorAll(selector);
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const delay = entry.target.dataset.delay || 0;
                setTimeout(() => {
                    entry.target.classList.add(className);
                }, parseInt(delay));
            }
        });
    }, { threshold: 0.1 });

    elements.forEach(el => observer.observe(el));
};

animateOnScroll('.feature-card');

// =====================
// SMOOTH NAV LINK HIGHLIGHT
// =====================
const sections = document.querySelectorAll('section[id]');
const navLinkEls = document.querySelectorAll('.nav-links a[href^="#"]');

window.addEventListener('scroll', () => {
    let current = '';
    sections.forEach(section => {
        const sectionTop = section.offsetTop - 80;
        if (window.scrollY >= sectionTop) {
            current = section.getAttribute('id');
        }
    });

    navLinkEls.forEach(a => {
        a.style.color = '';
        if (a.getAttribute('href') === `#${current}`) {
            a.style.color = '#2563eb';
        }
    });
});

// =====================
// TYPING EFFECT IN HERO CODE BLOCKS
// =====================
document.addEventListener('DOMContentLoaded', () => {
    console.log('%c📚 Library Management System', 'font-size:18px;font-weight:bold;color:#2563eb;');
    console.log('%c☕ Built with Java 21 | SQLite JDBC | Multithreading | OOP', 'color:#64748b;');
    console.log('%c🔗 GitHub: https://github.com/chandraabhinav68-pixel/Libray-Management-System', 'color:#22c55e;');
});
