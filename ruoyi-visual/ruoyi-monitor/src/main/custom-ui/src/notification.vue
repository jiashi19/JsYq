<script>
  export default {
  data() {
  return {
  messages: [], // 存储收到的消息
  eventSource: null, // 用于保存 EventSource 实例
};
},
  mounted() {
  this.connectSSE(); // 组件挂载时建立 SSE 连接
},
  beforeUnmount() {
  this.closeSSE(); // 组件销毁时关闭 SSE 连接
},
  methods: {
  // 建立 SSE 连接
  connectSSE() {
  // 创建 EventSource 实例，连接到后端的 SSE 端点
  this.eventSource = new EventSource("/sse");

  // 监听消息事件
  this.eventSource.onmessage = (event) => {
  this.messages.push(event.data); // 将收到的消息添加到列表中
};

  // 监听错误事件
  this.eventSource.onerror = (error) => {
  console.error("SSE 连接错误:", error);
  this.closeSSE(); // 关闭连接
  setTimeout(() => {
  this.connectSSE(); // 尝试重新连接
}, 5000); // 5秒后重试
};
},

  // 关闭 SSE 连接
  closeSSE() {
  if (this.eventSource) {
  this.eventSource.close(); // 关闭 EventSource
  this.eventSource = null;
}
},
},
};

</script>

<template>
  <div>
    <h1>SSE 实时消息推送</h1>
    <div>
      <p>收到的消息：</p>
      <ul>
        <li v-for="(message, index) in messages" :key="index">{{ message }}</li>
      </ul>
    </div>
  </div>
</template>
<style scoped>
h1 {
  font-size: 24px;
  margin-bottom: 20px;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  padding: 8px;
  border-bottom: 1px solid #eee;
}
</style>