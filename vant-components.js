// Vant组件配置文件
import { 
  Switch, 
  Image, 
  Button, 
  Cell, 
  CellGroup, 
  Field, 
  Toast, 
  Dialog,
  NavBar,
  Uploader
} from 'vant';

// 导出需要注册的组件
export const vantComponents = {
  'van-switch': Switch,
  'van-image': Image,
  'van-button': Button,
  'van-cell': Cell,
  'van-cell-group': CellGroup,
  'van-field': Field,
  'van-toast': Toast,
  'van-dialog': Dialog,
  'van-nav-bar': NavBar,
  'van-uploader': Uploader
};

// 导出组件实例（用于直接调用）
export { 
  Switch, 
  Image, 
  Button, 
  Cell, 
  CellGroup, 
  Field, 
  Toast, 
  Dialog,
  NavBar,
  Uploader
}; 