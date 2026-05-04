let typingSpeed = 20;
const BASE_URL = "http://localhost:8080";
    function typeText(element, text) {
        let i = 0;
        element.html("");

        function typing() {
            if (i < text.length) {
                element.html(element.html() + text.charAt(i));
                i++;
                setTimeout(typing, typingSpeed);
            }
        }

        typing();
    }

    function addMessage(content, type) {
        let msgClass = type === 'user' ? 'user-msg' : 'bot-msg';
        let bubbleClass = type === 'user' ? 'user-bubble' : 'bot-bubble';

        let msg = $(`
            <div class="${msgClass}">
                <div class="bubble ${bubbleClass}"></div>
            </div>
        `);

        $("#chatBox").append(msg);
        $("#chatBox").scrollTop($("#chatBox")[0].scrollHeight);

        if (type === 'bot') {
            typeText(msg.find(".bubble"), content);
        } else {
            msg.find(".bubble").text(content);
        }
    }

    // 🌿 Identify Plant
    $("#identifyBtn").click(function () {
        let file = $("#imageInput")[0].files[0];

        if (!file) {
            alert("Select an image first");
            return;
        }

        let formData = new FormData();
        formData.append("image", file);

        $("#plantResult").html(" Identifying...");

        $.ajax({
            url: BASE_URL+"/identify",
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
             xhrFields: {
                    withCredentials: true
                },
            success: function (data) {

//            console.log(data)
//            console.log(typeof(data))
                if (typeof data === "string") {
                    $("#plantResult").html(`<div class="alert alert-warning">${data}</div>`);
                } else {
                   let html = `<div class="card p-3">`;

                      for (let k in data) {
                          let v = data[k];
                          if (!v) continue;

                          let label = k.replace(/([A-Z])/g, ' $1').replace(/^./, s => s.toUpperCase());

                          if (Array.isArray(v)) {
                              html += `<h6>${label}</h6><ul>${v.map(x => `<li>${x}</li>`).join("")}</ul>`;
                          } else {
                              html += `<p><b>${label}:</b> ${v === true ? "Yes" : v === false ? "No" : v}</p>`;
                          }
                      }

                      html += `</div>`;
                      $("#plantResult").html(html);
                  }
            },
            error: function () {
                $("#plantResult").html(`<div class="alert alert-danger">Error identifying plant</div>`);
            }
        });
    });

    // 💬 Ask Question
    $("#sendBtn").click(function () {
        let question = $("#questionInput").val();

        if (!question) return;

        addMessage(question, "user");
        $("#questionInput").val("");

        $.ajax({
            url: BASE_URL+"/ask",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                content: question,
                role: "USER"
            }),
             xhrFields: {
                    withCredentials: true
                },
            success: function (response) {
                addMessage(response, "bot");
            },
            error: function (xhr) {
                addMessage(xhr.responseText || "Error", "bot");
            }
        });
    });

//reset session on window reload
window.onload = function () {
    fetch(BASE_URL + "/reset", {
        method: "POST",
        credentials: "include"
    });
};

    // Enter key support
    $("#questionInput").keypress(function (e) {
        if (e.which === 13) {
            $("#sendBtn").click();
        }
    });



