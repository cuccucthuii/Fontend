// Bridge external code to project-wide API client and endpoints
import coreApi, { API_BASE_URL } from "../../services/api";
export default coreApi;
export { API_BASE_URL };
export * from "../../constants/api";
