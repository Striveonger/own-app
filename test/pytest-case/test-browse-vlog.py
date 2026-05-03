import pytest
import requests

class TestBrowseVlog:
    """
    测试查询Vlog列表接口
    """
    @pytest.fixture(autouse=True)
    def setup(self, base_url):
        self.base_url = base_url

    def test_get_vlog_list(self, user_id):
        """
        获取用户推荐Vlog列表
        """
        response = requests.get(
                f"{self.base_url}/api/v1/vlog/list",
                params={"userId": user_id}
            )

        assert response.status_code == 200
        data = response.json()
        assert data["message"] == "Success"
        assert len(data["data"]) > 0

        # for vlog_id in data["data"]:
        #     assert vlog_id is not None and len(vlog_id) >= 17
