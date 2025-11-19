document.addEventListener('DOMContentLoaded', () => {

    const largeTabs = document.querySelectorAll('.large-tab .mate-tab-button');
    const smallTabs = document.querySelectorAll('.small-tab .mate-tab-button');
    const sections = document.querySelectorAll('.mate-section');
    const sectionTitles = document.querySelectorAll('.mate-section-title');
    const soloCards = document.querySelectorAll('.mate-card.solo');
    const crewCards = document.querySelectorAll('.mate-card.crew');

    let currentSport = 'running'; // 'running' or 'weight'
    let currentType = 'solo'; // 'solo' or 'group'

    // 필터링 함수
    function filterMates() {
        // 1. 섹션(1:1 / 그룹) 보여주기/숨기기
        sections.forEach(section => {
            if (section.id === `${currentType}-section`) {
                section.classList.add('active');
            } else {
                section.classList.remove('active');
            }
        });

        // 2. 섹션 제목 변경 (예: "1:1 러닝 메이트", "웨이트 크루")
        const sportText = currentSport === 'running' ? '러닝' : '웨이트';
        sectionTitles[0].textContent = `1:1 ${sportText} 메이트`; // solo-section의 h2
        sectionTitles[1].textContent = `${sportText} 크루`;      // group-section의 h2

        // 3. 카드 필터링 (data-sport 속성 이용)
        soloCards.forEach(card => {
            if (card.dataset.sport === currentSport) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });

        crewCards.forEach(card => {
            if (card.dataset.sport === currentSport) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });
    }

    // 큰 탭 (러닝/웨이트) 이벤트
    largeTabs.forEach(tab => {
        tab.addEventListener('click', () => {
            largeTabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            currentSport = tab.dataset.tabFilter;
            filterMates();
        });
    });

    // 작은 탭 (1:1/그룹) 이벤트
    smallTabs.forEach(tab => {
        tab.addEventListener('click', () => {
            smallTabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            currentType = tab.dataset.tabFilter;
            filterMates();
        });
    });

    // 페이지 처음 로드 시 기본 필터(러닝, 1:1) 실행
    filterMates();
});