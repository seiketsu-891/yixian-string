// export const EMAIL_PATTERN = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/
export const PASSWORD_PATTERN = /^[\x20-\x7E]{8,16}$/
export const USERNAME_PATTERN = /^[\u4e00-\u9fa5_a-zA-Z0-9]{3,16}$/
export const MOBILE_PATTERN = /^[1][3,4,5,6,7,8,9][0-9]{9}$/
export const CODE_PATTERN = /^[0-9]{6}$/