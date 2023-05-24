import numpy as np
import torch.optim

TOLERANCE = 1e-2

functions = [lambda x: (x - 1) ** 2 for _ in range(0, 50)] + \
            [lambda x: (2 * x - 1) ** 2 for _ in range(50, 100)] + \
            [lambda x: (3 * x - 1) ** 2 for _ in range(100, 150)] + \
            [lambda x: (4 * x - 1) ** 2 for _ in range(150, 200)]

func = lambda x: sum([functions[a](x[a]) for a in range(0, 200)])

dim = 200
x = torch.tensor([2. for _ in range(dim)], requires_grad=True)

batch_size = 200
train_loader = torch.utils.data.DataLoader(x, batch_size=batch_size)
it = iter(train_loader)

optimizer = torch.optim.Adam([x], lr=0.6, betas=(0.001, 0.99))
# optimizer = torch.optim.RMSprop([x], lr=0.2, momentum=0.1)
# optimizer = torch.optim.Adagrad([x], lr=0.9)
# optimizer = torch.optim.SGD([x], lr=0.057)

epoch = 100
points = np.zeros((epoch, dim))
points_ptr = 0

curr_func_pointer = 0
for i in range(0, epoch * dim // batch_size):
    try:
        point = next(it)
    except StopIteration:
        it = iter(train_loader)
        point = next(it)

    optimizer.zero_grad()
    f = sum([functions[k + curr_func_pointer](point[k]) for k in range(0, batch_size)])

    f.backward()
    optimizer.step()

    if curr_func_pointer == 0:

        points[points_ptr] = x.data
        points_ptr += 1

        array = np.array([it.item() for it in x])
        norm = np.linalg.norm(
            (func(array + 1e-5 * np.eye(dim)) - func(array - 1e-5 * np.eye(dim))) / (2 * 1e-5)
        )
        print(norm)
        if norm < TOLERANCE:
            points = points[0: points_ptr]
            break

    curr_func_pointer = (curr_func_pointer + batch_size) % dim

print(points[-1])
print(len(points))
