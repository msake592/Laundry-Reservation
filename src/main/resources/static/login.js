async function login() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    try {
        const response = await fetch("/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        const responseText = await response.text();
        console.log("Login response status:", response.status);
        console.log("Login response body:", responseText);

        if (!response.ok) {
            throw new Error("Login request failed with status " + response.status);
        }

        const data = responseText ? JSON.parse(responseText) : {};
        const token = data.token || data.jwt || data.accessToken;

        if (!token) {
            throw new Error("Token was not found in login response");
        }

        localStorage.setItem("token", token);
        window.location.href = "index.html";
    } catch (error) {
        console.error("Login error:", error);
        alert("Login failed. Check the browser console for details.");
    }
}