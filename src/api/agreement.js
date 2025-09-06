import request from './request'

/**
 * 协议相关API接口
 */
export const agreementApi = {
  /**
   * 获取用户服务协议
   */
  getUserAgreement() {
    return request({
      url: '/agreements/user-agreement',
      method: 'get'
    })
  },

  /**
   * 获取隐私政策
   */
  getPrivacyPolicy() {
    return request({
      url: '/agreements/privacy-policy',
      method: 'get'
    })
  },

  /**
   * 获取会员协议
   */
  getMembershipAgreement() {
    return request({
      url: '/agreements/membership-agreement',
      method: 'get'
    })
  },

  /**
   * 获取服务条款
   */
  getTermsOfService() {
    return request({
      url: '/agreements/terms-of-service',
      method: 'get'
    })
  },

  /**
   * 根据类型获取协议
   * @param {string} typeCode 协议类型代码
   */
  getAgreementByType(typeCode) {
    return request({
      url: `/agreements/${typeCode}`,
      method: 'get'
    })
  },

  /**
   * 获取所有协议类型列表
   */
  getAgreementTypes() {
    return request({
      url: '/agreements/types',
      method: 'get'
    })
  },

  /**
   * 获取协议摘要信息
   */
  getAgreementsSummary() {
    return request({
      url: '/agreements/summary',
      method: 'get'
    })
  }
}

export default agreementApi 