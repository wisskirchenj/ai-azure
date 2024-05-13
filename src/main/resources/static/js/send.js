const send = async () => {
  let prompt = document.getElementById("prompt").value;
  let data = document.getElementById("data").value;

  const response = await fetch(
      `/chat-data?prompt=${prompt}&data=${data}`, {
        method: "GET"
      });
  await handleResponse(response);
}

const sendRag = async () => {
  let prompt = document.getElementById("prompt").value;

  const response = await fetch(
      `/rag?prompt=${prompt}`, {
        method: "GET"
      });
  await handleResponse(response);
}

const sendQuiz = async () => {
  let topic = document.getElementById("topic").value;
  let amount = document.getElementById("number").value;

  const response = await fetch(
      `/quiz/${topic}?questions=${amount}`, {
        method: "GET"
      });
  if (response.ok) {
    const chatResponse = await response.json();
    const prettyChatResponse = JSON.stringify(chatResponse, null, 2);
    const message = document.getElementById("message");
    message.innerHTML = `<pre><code>${prettyChatResponse}</code></pre>`
  } else {
    flagError();
  }
}

const handleResponse = async response => {
  if (response.ok) {
    const chatResponse = await response.text();
    const message = document.getElementById("message");
    message.value = chatResponse;
  } else {
    flagError();
  }
}

const flagError = () => {
  const err = document.createElement("h2");
  err.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;Error calling LLM !";
  document.body.appendChild(err);
}

const cleanResponse = () => {
  const message = document.getElementById("message");
  message.value = '';
}