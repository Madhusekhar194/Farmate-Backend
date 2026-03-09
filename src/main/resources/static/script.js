// Real-time slider values
const sliders = ['N', 'P', 'K', 'temperature', 'humidity', 'ph', 'rainfall'];
sliders.forEach(name => {
  const input = document.querySelector(`input[name=${name}]`);
  const display = document.getElementById(`${name === 'temperature' ? 'temp' : name}Val`);
  input.addEventListener('input', () => {
    display.textContent = input.value;
  });
});

// Form submission
document.getElementById("cropForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  const formData = new FormData(this);
  const data = Object.fromEntries(formData.entries());
  for (let key in data) data[key] = parseFloat(data[key]);

  const resultEl = document.getElementById("result");
  resultEl.textContent = "⏳ Predicting...";

  try {
    const res = await fetch("http://localhost:9090/predict", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    if(!res.ok) throw new Error("Backend error");

    const result = await res.json();
    resultEl.textContent = `🌱 Recommended Crop: ${result.recommendedCrop}`;
  } catch (error) {
    console.error(error);
    resultEl.textContent = "❌ Error connecting to backend!";
  }
});
