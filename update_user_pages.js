const fs = require('fs');
const path = require('path');

// Danh sách các trang USER cần cập nhật
const userPages = [
  'src/pages/USER/NewsPage.vue',
  'src/pages/USER/TicketPricePage.vue', 
  'src/pages/USER/PromotionsPage.vue',
  'src/pages/AboutPage.vue',
  'src/pages/ContactPage.vue'
];

// Template cho Header với props
const headerTemplate = `  <Header 
    :is-logged-in="isLoggedIn"
    :user-info="userInfo"
    @show-auth-modal="handleShowAuthModal"
    @logout-success="handleLogoutSuccess"
  />`;

// Template cho AuthModal
const authModalTemplate = `
  <!-- Auth Modal -->
  <AuthModal 
    :show="showAuthModal"
    @close="showAuthModal = false"
    @login-success="handleLoginSuccess"
  />`;

// Script imports cần thêm
const authModalImport = `import AuthModal from '@/components/AuthModal.vue'`;

// Login state management code
const loginStateCode = `
// Login state management
const isLoggedIn = ref(false)
const userInfo = ref({})
const showAuthModal = ref(false)

// Load login state from localStorage
function loadLoginState() {
  isLoggedIn.value = localStorage.getItem('isLoggedIn') === 'true'
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    try {
      userInfo.value = JSON.parse(storedUserInfo)
    } catch (error) {
      console.error('Error parsing userInfo:', error)
      userInfo.value = {}
    }
  }
}

// Auth modal handlers
function handleShowAuthModal(type) {
  showAuthModal.value = true
}

function handleLoginSuccess(userData) {
  isLoggedIn.value = true
  userInfo.value = userData
  showAuthModal.value = false
}

function handleLogoutSuccess() {
  isLoggedIn.value = false
  userInfo.value = {}
}`;

function updateUserPage(filePath) {
  try {
    let content = fs.readFileSync(filePath, 'utf8');
    
    // 1. Cập nhật Header component
    content = content.replace(
      /<Header \/>/g,
      headerTemplate
    );
    
    // 2. Thêm AuthModal vào cuối template (trước </template>)
    if (!content.includes('AuthModal')) {
      content = content.replace(
        /<\/template>/,
        authModalTemplate + '\n</template>'
      );
    }
    
    // 3. Thêm import AuthModal
    if (!content.includes('import AuthModal')) {
      content = content.replace(
        /import.*from 'vue'/,
        `import { ref, computed, onMounted } from 'vue'`
      );
      content = content.replace(
        /import.*from 'vue'/,
        `import { ref, computed, onMounted } from 'vue'\nimport AuthModal from '@/components/AuthModal.vue'`
      );
    }
    
    // 4. Thêm login state management code
    if (!content.includes('Login state management')) {
      // Tìm vị trí sau các ref declarations
      const refMatch = content.match(/(const \w+ = ref\([^)]*\)\s*)+/);
      if (refMatch) {
        const insertPos = refMatch.index + refMatch[0].length;
        content = content.slice(0, insertPos) + loginStateCode + content.slice(insertPos);
      }
    }
    
    // 5. Thêm loadLoginState() vào onMounted
    if (content.includes('onMounted') && !content.includes('loadLoginState()')) {
      content = content.replace(
        /onMounted\(async \(\) => \{/,
        'onMounted(async () => {\n  loadLoginState() // Load login state first'
      );
    }
    
    fs.writeFileSync(filePath, content);
    console.log(`✅ Updated: ${filePath}`);
    
  } catch (error) {
    console.error(`❌ Error updating ${filePath}:`, error.message);
  }
}

// Cập nhật tất cả các trang
console.log('🔄 Updating USER pages with login state management...\n');

userPages.forEach(updateUserPage);

console.log('\n✅ All USER pages updated successfully!');



