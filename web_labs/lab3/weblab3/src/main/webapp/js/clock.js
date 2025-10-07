function tick(){
  let el = document.getElementById('clock');
  if(!el) return;
  let d = new Date();
  el.textContent = d.toLocaleString();
}
window.addEventListener('load', function(){
  tick();
  setInterval(tick, 6000);
});

