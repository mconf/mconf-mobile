#include <common.h>
#include "gtest_QueueDiscardPolicy.h"

TEST_F(QueueDiscardPolicyTest, Initialization)
{
    QueueDiscardPolicy * policy = new QueueDiscardPolicy();
    delete policy;
}

TEST_F(QueueDiscardPolicyTest, AddAndRemoveItems)
{
    QueueDiscardPolicy policy;
    EXPECT_EQ(E_OK, policy.add(6, 4))  << "Retorno deveria ser E_OK";
    EXPECT_EQ(E_OK, policy.add(8, 3))  << "Retorno deveria ser E_OK";
    EXPECT_EQ(E_OK, policy.add(11, 2)) << "Retorno deveria ser E_OK";
    EXPECT_EQ(E_OK, policy.add(15, 1)) << "Retorno deveria ser E_OK";
    EXPECT_EQ(E_OK, policy.add(20, 0)) << "Retorno deveria ser E_OK";
    EXPECT_NE(E_OK, policy.add(8, 0))  << "Retorno NAO deveria ser E_OK"; // duplicado
    EXPECT_NE(E_OK, policy.add(6, 0))  << "Retorno NAO deveria ser E_OK"; // duplicado

    EXPECT_EQ(5, policy.size());

    ASSERT_TRUE(policy.exists(6));
    ASSERT_TRUE(policy.exists(8));
    ASSERT_TRUE(policy.exists(11));
    ASSERT_TRUE(policy.exists(15));
    ASSERT_TRUE(policy.exists(20));
    ASSERT_FALSE(policy.exists(30)); // não existente
    ASSERT_FALSE(policy.exists(31)); // não existente
    ASSERT_FALSE(policy.exists(32)); // não existente

    policy.remove(6);  ASSERT_FALSE(policy.exists(6));
    policy.remove(8);  ASSERT_FALSE(policy.exists(8));
    policy.remove(11); ASSERT_FALSE(policy.exists(11));
    policy.remove(15); ASSERT_FALSE(policy.exists(15));
    policy.remove(20); ASSERT_FALSE(policy.exists(20));

    ASSERT_EQ(0, policy.size());
}

TEST_F(QueueDiscardPolicyTest, CheckFramesToUse)
{
    QueueDiscardPolicy policy;
    policy.add(6, 4);
    policy.add(8, 3);
    policy.add(11, 2);
    policy.add(15, 1);
    policy.add(20, 0);
    policy.add(30, QueueDiscardPolicy::FLAG_CLEAR);

    EXPECT_EQ(QueueDiscardPolicy::FLAG_ALL, policy.getFramesToUse(0));
    EXPECT_EQ(QueueDiscardPolicy::FLAG_ALL, policy.getFramesToUse(1));
    EXPECT_EQ(QueueDiscardPolicy::FLAG_ALL, policy.getFramesToUse(2));
    EXPECT_EQ(QueueDiscardPolicy::FLAG_ALL, policy.getFramesToUse(3));
    EXPECT_EQ(QueueDiscardPolicy::FLAG_ALL, policy.getFramesToUse(4));
    EXPECT_EQ(QueueDiscardPolicy::FLAG_ALL, policy.getFramesToUse(5));

    EXPECT_EQ(4, policy.getFramesToUse(6));
    EXPECT_EQ(4, policy.getFramesToUse(7));

    EXPECT_EQ(3, policy.getFramesToUse(8));
    EXPECT_EQ(3, policy.getFramesToUse(9));
    EXPECT_EQ(3, policy.getFramesToUse(10));

    EXPECT_EQ(2, policy.getFramesToUse(11));
    EXPECT_EQ(2, policy.getFramesToUse(12));
    EXPECT_EQ(2, policy.getFramesToUse(13));
    EXPECT_EQ(2, policy.getFramesToUse(14));

    EXPECT_EQ(1, policy.getFramesToUse(15));
    EXPECT_EQ(1, policy.getFramesToUse(16));
    EXPECT_EQ(1, policy.getFramesToUse(17));
    EXPECT_EQ(1, policy.getFramesToUse(18));
    EXPECT_EQ(1, policy.getFramesToUse(19));

    EXPECT_EQ(0, policy.getFramesToUse(20));
    EXPECT_EQ(0, policy.getFramesToUse(21));
    EXPECT_EQ(0, policy.getFramesToUse(29));

    EXPECT_EQ(QueueDiscardPolicy::FLAG_CLEAR, policy.getFramesToUse(30));
    EXPECT_EQ(QueueDiscardPolicy::FLAG_CLEAR, policy.getFramesToUse(50));
    EXPECT_EQ(QueueDiscardPolicy::FLAG_CLEAR, policy.getFramesToUse(200));
}

TEST_F(QueueDiscardPolicyTest, CheckLevel)
{
    QueueDiscardPolicy policy;
    policy.add(6, 4);
    policy.add(8, 3);
    policy.add(11, 2);
    policy.add(15, 1);
    policy.add(20, 0);
    policy.add(30, QueueDiscardPolicy::FLAG_CLEAR);

    EXPECT_EQ(0, policy.getLevel(0));
    EXPECT_EQ(0, policy.getLevel(1));
    EXPECT_EQ(0, policy.getLevel(2));
    EXPECT_EQ(0, policy.getLevel(3));
    EXPECT_EQ(0, policy.getLevel(4));
    EXPECT_EQ(0, policy.getLevel(5));
    EXPECT_EQ(1, policy.getLevel(6));
    EXPECT_EQ(1, policy.getLevel(7));
    EXPECT_EQ(2, policy.getLevel(8));
    EXPECT_EQ(2, policy.getLevel(9));
    EXPECT_EQ(2, policy.getLevel(10));
    EXPECT_EQ(3, policy.getLevel(11));
    EXPECT_EQ(3, policy.getLevel(12));
    EXPECT_EQ(3, policy.getLevel(13));
    EXPECT_EQ(3, policy.getLevel(14));
    EXPECT_EQ(4, policy.getLevel(15));
    EXPECT_EQ(4, policy.getLevel(16));
    EXPECT_EQ(4, policy.getLevel(17));
    EXPECT_EQ(4, policy.getLevel(18));
    EXPECT_EQ(4, policy.getLevel(19));
    EXPECT_EQ(5, policy.getLevel(20));
    EXPECT_EQ(5, policy.getLevel(25));
    EXPECT_EQ(5, policy.getLevel(29));
    EXPECT_EQ(6, policy.getLevel(30));
    EXPECT_EQ(6, policy.getLevel(31));
    EXPECT_EQ(6, policy.getLevel(70));
}

TEST_F(QueueDiscardPolicyTest, ComparingOperators)
{
    QueueDiscardPolicy policy1;
    policy1.add(6, 4);
    policy1.add(8, 3);
    policy1.add(11, 2);

    QueueDiscardPolicy policy2;
    policy2.add(6, 4);
    policy2.add(8, 3);
    policy2.add(11, 2);

    QueueDiscardPolicy policy3;
    policy3.add(6, 3);
    policy3.add(8, 2);
    policy3.add(11, 1);

    QueueDiscardPolicy policy4;
    policy4.add(5, 4);
    policy4.add(9, 3);
    policy4.add(11, 2);

    // policy1 == policy2 e os outros 2 são diferentes de todos

    EXPECT_TRUE(policy1 == policy2);
    EXPECT_FALSE(policy1 == policy3);
    EXPECT_FALSE(policy2 == policy3);
    EXPECT_FALSE(policy1 == policy4);
    EXPECT_FALSE(policy2 == policy4);
    EXPECT_FALSE(policy3 == policy4);

    EXPECT_FALSE(policy1 != policy2);
    EXPECT_TRUE(policy1 != policy3);
    EXPECT_TRUE(policy2 != policy3);
    EXPECT_TRUE(policy1 != policy4);
    EXPECT_TRUE(policy2 != policy4);
    EXPECT_TRUE(policy3 != policy4);
}

TEST_F(QueueDiscardPolicyTest, GetItemByLevel)
{
    pair<int, int> target, result;
    QueueDiscardPolicy policy;

    // com o policy vazio deve sempre retornar esse par
    target = make_pair(0, QueueDiscardPolicy::FLAG_ALL);
    result = policy.getItemByLevel(0);
    EXPECT_EQ(target.first, result.first);
    EXPECT_EQ(target.second, result.second);
    result = policy.getItemByLevel(20);
    EXPECT_EQ(target.first, result.first);
    EXPECT_EQ(target.second, result.second);

    // agora insere items e testa cada nível

    policy.add(6, 4);
    policy.add(8, 3);
    policy.add(11, 2);
    policy.add(15, 1);
    policy.add(20, 0);
    policy.add(30, QueueDiscardPolicy::FLAG_CLEAR);

    target = make_pair(0, QueueDiscardPolicy::FLAG_ALL);
    result = policy.getItemByLevel(-20);
    EXPECT_EQ(target.first, result.first);
    EXPECT_EQ(target.second, result.second);
    result = policy.getItemByLevel(0);
    EXPECT_EQ(target.first, result.first);
    EXPECT_EQ(target.second, result.second);

    target = make_pair(6, 4);
    result = policy.getItemByLevel(1);
    EXPECT_EQ(target.first, result.first);
    EXPECT_EQ(target.second, result.second);

    target = make_pair(8, 3);
    result = policy.getItemByLevel(2);
    EXPECT_EQ(target.first, result.first);
    EXPECT_EQ(target.second, result.second);

    target = make_pair(11, 2);
    result = policy.getItemByLevel(3);
    EXPECT_EQ(target.first, result.first);
    EXPECT_EQ(target.second, result.second);

    target = make_pair(15, 1);
    result = policy.getItemByLevel(4);
    EXPECT_EQ(target.first, result.first);
    EXPECT_EQ(target.second, result.second);

    target = make_pair(20, 0);
    result = policy.getItemByLevel(5);
    EXPECT_EQ(target.first, result.first);
    EXPECT_EQ(target.second, result.second);

    target = make_pair(30, QueueDiscardPolicy::FLAG_CLEAR);
    result = policy.getItemByLevel(6);
    EXPECT_EQ(target.first, result.first);
    EXPECT_EQ(target.second, result.second);
    result = policy.getItemByLevel(18);
    EXPECT_EQ(target.first, result.first);
    EXPECT_EQ(target.second, result.second);
}

