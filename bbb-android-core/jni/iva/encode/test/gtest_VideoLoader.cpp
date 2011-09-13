#include <VideoLoader.h>
#include <IPv4.h>
#include <queue.h>
#include <gtest/gtest.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
using namespace std;

namespace {

  class VideoLoaderTest :
    public ::testing::Test
  {

  protected:
    
    VideoLoaderTest()
    {

      
      
    }

    virtual ~VideoLoaderTest() {
      
      

    }

    virtual void SetUp() {

      
      
    }

    virtual void TearDown() {
      
      

    }
    
  };
    
};

TEST_F(VideoLoaderTest, Start) {
    VideoLoader v;
    queue_t* qvideo = queue_create();
    queue_t* qaudio = queue_create();

    v.start(qvideo, qaudio, "video.avi");
    system("pause");
    v.stop();
}
