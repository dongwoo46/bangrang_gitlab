import axios from "axios";

const AccessToken = localStorage.getItem("AccessToken");

const axiosInstance = axios.create({
  headers: {
    "Content-Type": "application/json",
    Authorization: "Bearer " + AccessToken,
  },
});

axiosInstance.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const {
      config,
      response: { status },
    } = error;

    const originalRequest = config;

    if (status === 403) {
      const accessToken = localStorage.getItem("AccessToken");
      const refreshToken = localStorage.getItem("RefreshToken");

      try {
        const { data, headers } = await axios({
          method: 'post',
          url: `${process.env.REACT_APP_API}/refresh`,
          headers: {
            Authorization: accessToken,
            "Authorization-refresh": refreshToken,
          },
          data: { accessToken, refreshToken },
        });
        const newAccessToken = headers['authorization'];
        const newRefreshToken = headers['authorization-refresh'];
        originalRequest.headers = {
          "Content-Type": "application/json",
          Authorization: "Bearer " + newAccessToken,
        };
        localStorage.setItem("AccessToken", newAccessToken);
        localStorage.setItem("RefreshToken", newRefreshToken);
        return await axios(originalRequest);
      } catch (err) {
        console.log(err);
        localStorage.clear();
      }
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
