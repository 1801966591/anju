export function formatTimeToTime(timeString){
    // 使用 JavaScript Date 对象解析时间字符串
    const date = new Date(timeString);

    // 获取小时、分钟和秒
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');

    // 返回格式化后的时间字符串
    return `${hours}:${minutes}:${seconds}`;
}

export default function formatTimeToDateTime(timeString){
    // 使用 JavaScript Date 对象解析时间字符串
    const date = new Date(timeString);

    // 获取年、月、日、小时、分钟和秒
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    let day = date.getDate().toString().length === 1 ? '0' + date.getDate().toString() : date.getDate().toString();
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');

    // 返回格式化后的时间字符串
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}