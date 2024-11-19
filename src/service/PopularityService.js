import axiosInstance from "@/service/axiosInstance";
const PopularityService = {
    getPopularity(top) {
        return axiosInstance.get('/bots/popular', {
            params: {
                top: top
            }
        });
    },
    getLatest(top) {
        return axiosInstance.get('/bots/latest', {
            params: {
                top: top
            }
        });
    },
    getBestMonthly(top) {
        return axiosInstance.get('/bots/best/monthly', {
            params: {
                top: top
            }
        });
    },
    getBestHistorical(top) {
        return axiosInstance.get('/bots/best/historical', {
            params: {
                top: top
            }
        });
    }
}
export default PopularityService;